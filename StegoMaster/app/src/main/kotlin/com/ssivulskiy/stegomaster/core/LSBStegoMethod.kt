package com.ssivulskiy.stegomaster.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.ssivulskiy.stegomaster.utils.getBitAtPos
import com.ssivulskiy.stegomaster.utils.prepareMessage
import com.ssivulskiy.stegomaster.utils.setOneAtPos
import com.ssivulskiy.stegomaster.utils.setZeroAtPos
import java.io.File
import java.io.FileOutputStream

class LSBStegoMethod() {

    private val LOG_TAG = javaClass.simpleName

    private var mUsedBit = 1

    fun code(msg : String, inFile : File, outFile : File) {
        val msgByte = prepareMessage(msg)

        val options = BitmapFactory.Options().apply {
            inMutable = true
        }

        var bitmap = BitmapFactory.decodeFile(inFile.absolutePath, options)

        var byte = 0;
        var byteBit = 7
        var finish = false

        loop@for (y in 0..bitmap.height - 1) {
            for (x in 0..bitmap.width - 1) {

                var pixel = bitmap.getPixel(x, y)

                var alpha = Color.alpha(pixel)
                var red = Color.red(pixel)
                var green = Color.green(pixel)
                var blue = Color.blue(pixel)

                val colorArray = arrayOf(red, green, blue)

                for (colorItem in 0..colorArray.size - 1) {
                    if (byteBit == -1) {
                        byte++;
                        byteBit = 7
                        if (byte == msgByte.size) {
                            finish = true
                            break
                        }
                    }

                    val value = msgByte[byte].getBitAtPos(byteBit).toInt()
                    var color = colorArray[colorItem]

                    if (value == 1) {
                        color = color or 1
                    } else {
                        color = color and 1.inv()
                    }

                    colorArray[colorItem] = color



                    byteBit--
                }

                red = colorArray[0]
                green = colorArray[1]
                blue = colorArray[2]

                val newPixel = Color.argb(alpha, red, green, blue)

                bitmap.setPixel(x, y, newPixel)

                if (finish)
                    break@loop
            }
        }

        val fOut = FileOutputStream(outFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();

    }

    fun decode(file : File) : String {
        var bitmap = BitmapFactory.decodeFile(file.absolutePath)

        val msgByte = mutableListOf<Byte>()
        var byte: Byte = 0;
        var byteBit = 7
        var msgSize = -1;

        loop@for (y in 0..bitmap.height - 1) {
            for (x in 0..bitmap.width - 1) {

                var pixel = bitmap.getPixel(x, y)

                var alpha = Color.alpha(pixel)
                var red = Color.red(pixel)
                var green = Color.green(pixel)
                var blue = Color.blue(pixel)

                val colorArray = arrayOf(red, green, blue)

                for (colorItem in 0..colorArray.size - 1) {
                    if (byteBit == -1) {
                        msgByte.add(byte)
                        byte = 0
                        byteBit = 7
                        if (msgSize == -1 && msgByte.size == 2) {
                            msgSize = msgByte[0].toInt().shl(8).or(msgByte[1].toInt())
                            msgByte.clear()
                            Log.d(LOG_TAG, "Message size: $msgSize")
                        }

                    }

                    if (msgSize != -1 && msgSize == msgByte.size)
                        break@loop

                    if (colorArray[colorItem].toByte().getBitAtPos(0).toInt() == 0) {
                        byte = byte.setZeroAtPos(byteBit)
                    } else {
                        byte = byte.setOneAtPos(byteBit)
                    }


                    byteBit--;
                }

            }
        }
        val msg = String(msgByte.toByteArray())
        Log.d(LOG_TAG, "Message: $msg")

        return msg
    }

}
