package com.ssivulskiy.stegomaster.core

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.ssivulskiy.stegomaster.utils.calculateMessageLength
import com.ssivulskiy.stegomaster.utils.getBitAtPos
import com.ssivulskiy.stegomaster.utils.setOneAtPos
import com.ssivulskiy.stegomaster.utils.setZeroAtPos
import java.io.File
import java.io.FileOutputStream

class LSBPermutationMethod : IStegoMethod {

    private val LOG_TAG = javaClass.simpleName

    private val RED = 0
    private val GREEN = 1
    private val BLUE = 2


    //0b100 - red
    //0b010 - green
    //0b001 - blue
    var mComponents = 0b001

    override fun code(msgByte: List<Byte>, inFile: File, outFile: File) {

        val options = BitmapFactory.Options().apply {
            inMutable = true
        }

        var bitmap = BitmapFactory.decodeFile(inFile.absolutePath, options)

        for (i in 0..msgByte.size.times(8).minus(1)) {

            var x = Math.floor(i * 15 / bitmap.width.toDouble()).toInt()
            var y = i.times(15) % bitmap.height
            var pixel = bitmap.getPixel(x, y)

            Log.d(LOG_TAG, "X: $x, Y:$y")

            var alpha = Color.alpha(pixel)
            var red = Color.red(pixel)
            var green = Color.green(pixel)
            var blue = Color.blue(pixel)

            var key = BLUE
            var color = blue

            if (mComponents.shr(2).and(0x00000001) == 1) {
                key = RED
                color = red
            } else if (mComponents.shr(1).and(0x00000001) == 1) {
                key = GREEN
                color = green
            } else if (mComponents.and(0x00000001) == 1) {
                key = BLUE
                color = blue
            }


            val value = msgByte[i / 8].getBitAtPos(i % 8).toInt()
            var modif = false
            if (color.getBitAtPos(0) != value)
                modif = true

            if (value == 1) {
                color = color or 1
            } else {
                color = color and 1.inv()
            }

            when (key) {
                RED -> {
                    red = color
                }
                GREEN -> {
                    green = color
                }
                BLUE -> {
                    blue = color
                }
            }

            var newPixel = Color.argb(alpha, red, green, blue)
//            if (modif)
//                newPixel = Color.BLACK

            bitmap.setPixel(x, y, newPixel)

        }

        val fOut = FileOutputStream(outFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();
    }


    override fun decode(file: File): List<Byte> {
        var bitmap = BitmapFactory.decodeFile(file.absolutePath)

        var i = 0
        val msgByte = mutableListOf<Byte>()
        var msgSize = -1
        var byte: Byte = 0;

        while (true) {

            var x = Math.floor(i * 15 / bitmap.width.toDouble()).toInt()
            var y = i.times(15) % bitmap.height

            var pixel = bitmap.getPixel(x, y)

            Log.d(LOG_TAG, "X: $x, Y:$y")

            var alpha = Color.alpha(pixel)
            var red = Color.red(pixel)
            var green = Color.green(pixel)
            var blue = Color.blue(pixel)

            var color = blue

            if (mComponents.shr(2).and(0x00000001) == 1) {
                color = red
            } else if (mComponents.shr(1).and(0x00000001) == 1) {
                color = green
            } else if (mComponents.and(0x00000001) == 1) {
                color = blue
            }

            if (i % 8 == 0 && i != 0) {
                msgByte.add(byte)
                byte = 0
                if (msgSize == -1 && msgByte.size == 2) {
                    msgSize = decodeMsgSize(msgByte)
                    msgByte.clear()
                    Log.d(LOG_TAG, "Message size: $msgSize")
                }

            }

            if (msgSize != -1 && msgSize == msgByte.size)
                break

            if (color.toByte().getBitAtPos(0).toInt() == 0) {
                byte = byte.setZeroAtPos(i % 8)
            } else {
                byte = byte.setOneAtPos(i % 8)
            }

            i++
        }

        return msgByte
    }

    override fun decodeMsgSize(msg: List<Byte>): Int {
        return calculateMessageLength(msg)
    }

}