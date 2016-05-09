package com.ssivulskiy.stegomaster.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.ssivulskiy.stegomaster.utils.*
import java.io.File
import java.io.FileOutputStream

class QuantizationStegoMethod() : IStegoMethod {

    private val LOG_TAG = javaClass.simpleName
    private val QUANT_TABLE_SIZE = 255 * 2 + 1

    var mQuantizationTable = Array(QUANT_TABLE_SIZE, { 0 })

    init {
        val s = "qwerty1234stegoqwerty1234stegoqwerty1234stegoqwerty1234steg756".toByteArray()
        var k = 0
        for (i in 0..mQuantizationTable.size - 1) {
            mQuantizationTable[i] = s[i % 8].getBitAtPos(k).toInt()
            k = k.plus(1).mod(8)

        }

    }

    override fun code(msgByte: List<Byte>, inFile: File, outFile: File) {
        val options = BitmapFactory.Options().apply {
            inMutable = true
        }

        var bitmap = BitmapFactory.decodeFile(inFile.absolutePath, options)

        var byte = 0;
        var byteBit = 7

        loop@for (y in 0..bitmap.height - 1) {
            for (x in 0..bitmap.width - 2 step 2) {

                if (byteBit == -1) {
                    byte++;
                    byteBit = 7
                    if (byte == msgByte.size) {
                        break@loop
                    }
                }

                var pixelA = bitmap.getPixel(x, y)
                var pixelB = bitmap.getPixel(x + 1, y)

                var blueA = Color.blue(pixelA)
                var blueB = Color.blue(pixelB)

                val diff = blueA - blueB
                val value = msgByte[byte].getBitAtPos(byteBit).toInt()
                var offsetR = 0
                var offsetL = 0
                if (getQuantElement(diff) != value) {
                    for (i in diff + 255..QUANT_TABLE_SIZE - 1) {
                        offsetR++
                        if (mQuantizationTable[i] == value) {
                            break
                        }
                    }
                    for (i in diff + 255 - 1 downTo 0) {
                        offsetL++
                        if (mQuantizationTable[i] == value) {
                            break
                        }
                    }

//                    val offset = Math.min(offsetL, offsetR)
//                    if (blueB + offset < 255) {
//                        blueA = blueB + offset
//                    } else {
//                        blueB = blueA - offset
//                    }

                    if (offsetR < offsetL) {
                        if (blueB + offsetR < 255) {
                            blueA = blueB + offsetR
                            blueB = blueA - offsetR
                        } else {
                            blueB -= offsetR
                        }
                    } else {
                        if (blueA + offsetL < 255) {
                            blueB = blueA + offsetL
                        } else {
                            blueA += blueB - offsetL
                        }
                    }

                    pixelA = Color.argb(Color.alpha(pixelA), Color.red(pixelA), Color.green(pixelA), blueA)
                    pixelB = Color.argb(Color.alpha(pixelB), Color.red(pixelB), Color.green(pixelB), blueB)

                    bitmap.setPixel(x, y, pixelA)
                    bitmap.setPixel(x + 1, y, pixelB)
//
                }

                byteBit--;

            }
        }

        val fOut = FileOutputStream(outFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();

    }

    override fun decode(file: File): List<Byte> {
        var bitmap = BitmapFactory.decodeFile(file.absolutePath)

        val msgByte = mutableListOf<Byte>()
        var byte: Byte = 0;
        var byteBit = 7
        var msgSize = -1;

        loop@for (y in 0..bitmap.height - 1) {
            for (x in 0..bitmap.width - 2 step 2) {

                if (byteBit == -1) {
                    msgByte.add(byte)
                    byte = 0
                    byteBit = 7
                    if (msgSize == -1 && msgByte.size == 2) {
                        msgSize = decodeMsgSize(msgByte)
                        msgByte.clear()
                        Log.d(LOG_TAG, "Message size: $msgSize")
                    }

                    if (msgSize != -1 && msgSize == msgByte.size)
                        break@loop

                }

                var pixelA = bitmap.getPixel(x, y)
                var pixelB = bitmap.getPixel(x + 1, y)

                var blueA = Color.blue(pixelA)
                var blueB = Color.blue(pixelB)

                val diff = blueA - blueB

                if (getQuantElement(diff) == 0) {
                    byte = byte.setZeroAtPos(byteBit)
                } else {
                    byte = byte.setOneAtPos(byteBit)
                }

                byteBit--
            }
        }

        return msgByte
    }

    override fun decodeMsgSize(msg: List<Byte>): Int {
        return calculateMessageLength(msg)
    }

    private fun getQuantElement(i: Int): Int {
        return mQuantizationTable[i + 255]
    }


}
