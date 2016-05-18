package com.ssivulskiy.stegomaster.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.ssivulskiy.stegomaster.utils.*
import java.io.File
import java.io.FileOutputStream

open class LSBMethod() : IStegoMethod {

    private val LOG_TAG = javaClass.simpleName

    private val RED = 0
    private val GREEN = 1
    private val BLUE = 2


    //0b1xx - red
    //0bx1x - green
    //0bxx1 - blue
    var mComponents = 0b001

    override fun code(msgByte : List<Byte>, inFile : File, outFile : File) {
//        val msgByte = makeStegoMessage(msg)

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
                Log.d(LOG_TAG, "X: $x, Y:$y")

                var alpha = Color.alpha(pixel)
                var red = Color.red(pixel)
                var green = Color.green(pixel)
                var blue = Color.blue(pixel)

//                val colorArray = arrayOf(red, green, blue)
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

                var modif = false
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
                        modif = true

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


                var newPixel = Color.argb(alpha, red, green, blue)

                if (modif)
                    newPixel = Color.BLACK

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

//                val colorArray = arrayOf(red, green, blue)

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
                            msgSize = decodeMsgSize(msgByte)
                            msgByte.clear()
                            Log.d(LOG_TAG, "Message size: $msgSize")
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

    override fun decodeMsgSize(msg: List<Byte>): Int {
        return calculateMessageLength(msg)
    }

}