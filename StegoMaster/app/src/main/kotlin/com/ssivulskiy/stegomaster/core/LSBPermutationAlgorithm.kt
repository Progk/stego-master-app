package com.ssivulskiy.stegomaster.core

import android.content.Intent
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.ssivulskiy.stegomaster.core.base.BaseStegoLsbAlgorithm
import com.ssivulskiy.stegomaster.utils.calculateMessageLength
import com.ssivulskiy.stegomaster.utils.getBitAtPos
import com.ssivulskiy.stegomaster.utils.setOneAtPos
import com.ssivulskiy.stegomaster.utils.setZeroAtPos
import java.io.File
import java.io.FileOutputStream

class LSBPermutationAlgorithm : BaseStegoLsbAlgorithm() {

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

            var pixelChanged = false

            if (color.getBitAtPos(0) != value)
                pixelChanged = true

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

            var newPixel : Int

            if (pixelChanged && mIsShowChangedPixels)
                newPixel = Color.BLACK
            else
                newPixel = Color.argb(alpha, red, green, blue)

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
                    msgSize = decodeMessageSize(msgByte)
                    msgByte.clear()
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

    override fun decodeMessageSize(msg: List<Byte>): Int {
        return calculateMessageLength(msg)
    }

}