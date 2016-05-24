package com.ssivulskiy.stegomaster.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.ssivulskiy.stegomaster.core.base.BaseStegoLsbAlgorithm
import com.ssivulskiy.stegomaster.utils.*
import java.io.File
import java.io.FileOutputStream

open class LSBAlgorithm() : BaseStegoLsbAlgorithm() {

    //0b1xx - red
    //0bx1x - green
    //0bxx1 - blue
    var mComponents = 0b001




    override fun code(msgByte : List<Byte>, inFile : File, outFile : File) {

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

                val colorMap = mutableMapOf<Int, Int>()

                if (mComponents.shr(2).and(0x00000001) == 1) {
                    colorMap[RED] = red
                }

                if (mComponents.shr(1).and(0x00000001) == 1) {
                    colorMap[GREEN] = green
                }

                if (mComponents.and(0x00000001) == 1) {
                    colorMap[BLUE] = blue
                }

                var pixelChanged = false

                for ((key, pix) in colorMap) {
                    if (byteBit == -1) {
                        byte++;
                        byteBit = 7
                        if (byte == msgByte.size) {
                            finish = true
                            break
                        }
                    }

                    val value = msgByte[byte].getBitAtPos(byteBit).toInt()
                    var color = pix

                    if (color.getBitAtPos(0) != value)
                        pixelChanged = true

                    if (value == 1) {
                        color = color or 1
                    } else {
                        color = color and 1.inv()
                    }

                    colorMap[key] = color

                    byteBit--
                }


                if (mComponents.shr(2).and(0x00000001) == 1) {
                    red = colorMap[RED]!!
                }

                if (mComponents.shr(1).and(0x00000001) == 1) {
                    green = colorMap[GREEN]!!
                }

                if (mComponents.and(0x00000001) == 1) {
                    blue = colorMap[BLUE]!!
                }


                var newPixel : Int

                if (pixelChanged && mIsShowChangedPixels)
                    newPixel = Color.BLACK
                else
                    newPixel = Color.argb(alpha, red, green, blue)

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

    override fun decode(file : File) : List<Byte> {
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

                val colorList = mutableListOf<Int>()

                if (mComponents.shr(2).and(0x00000001) == 1) {
                    colorList.add(red)
                }

                if (mComponents.shr(1).and(0x00000001) == 1) {
                    colorList.add(green)
                }

                if (mComponents.and(0x00000001) == 1) {
                    colorList.add(blue)
                }

                for (colorItem in 0..colorList.size - 1) {
                    if (byteBit == -1) {
                        msgByte.add(byte)
                        byte = 0
                        byteBit = 7
                        if (msgSize == -1 && msgByte.size == 2) {
                            msgSize = decodeMessageSize(msgByte)
                            msgByte.clear()
                        }

                    }

                    if (msgSize != -1 && msgSize == msgByte.size)
                        break@loop

                    if (colorList[colorItem].toByte().getBitAtPos(0).toInt() == 0) {
                        byte = byte.setZeroAtPos(byteBit)
                    } else {
                        byte = byte.setOneAtPos(byteBit)
                    }

                    byteBit--;
                }

            }
        }

        return msgByte
    }

    override fun decodeMessageSize(msg: List<Byte>): Int {
        return calculateMessageLength(msg)
    }

}
