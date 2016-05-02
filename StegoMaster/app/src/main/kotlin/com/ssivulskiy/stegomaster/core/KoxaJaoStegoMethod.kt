package com.ssivulskiy.stegomaster.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.ssivulskiy.stegomaster.fragments.KoxaJaoFragment
import com.ssivulskiy.stegomaster.fragments.StegoLsbFragment
import com.ssivulskiy.stegomaster.utils.*
import java.io.File
import java.io.FileOutputStream

class KoxaJaoStegoMethod() {

    private val LOG_TAG = javaClass.simpleName

    //coefficient 1
    private var coef1 = Coefficient(3, 4)

    //coefficient 2
    private var coef2 = Coefficient(4, 3)

    private var p = 25 //P

    private var matrixSize = 8 //N


    fun code(msg : String, inFile : File, outFile : File) {

        val msgByte = prepareMessage(msg)

        val options = BitmapFactory.Options().apply {
            inMutable = true
        }

        var bitmap = BitmapFactory.decodeFile(inFile.absolutePath, options)

        var byte = 0;
        var byteBit = 7

        loop@for (y in 0..bitmap.height - 1 step matrixSize) {
            for (x in 0..bitmap.width - 1 step matrixSize) {

                val arr = Array2dOfInt(matrixSize, matrixSize)
                for (i in 0..arr.size-1) {
                    for (j in 0..arr[i].size-1) {
                        val pixel = bitmap.getPixel(y + i, x + j)
                        arr[i][j] = Color.blue(pixel)
                    }
                }

                var dctCof = DCT(arr)

                if (byteBit == -1) {
                    byte++;
                    byteBit = 7
                    if (byte == msgByte.size) {
                        break@loop
                    }
                }

                val value = msgByte[byte]

                var cof1 = dctCof[coef1.x][coef1.y]
                var cof2 = dctCof[coef2.x][coef2.y]

                if (value.getBitAtPos(byteBit) == 0.toByte()) { //0
                    while (Math.abs(cof1) - Math.abs(cof2) <= p) {
                        if (cof1 < 0 && cof2 < 0) {
                            cof1--
                            cof2++
                        } else if (cof1 >= 0 && cof2 >= 0) {
                            cof1++
                            cof2--
                        } else if (cof1 < 0 && cof2 >= 0) {
                            cof1--
                            cof2--
                        } else if (cof1 >= 0 && cof2 < 0) {
                            cof1++
                            cof2++
                        }
                    }
                } else { //1
                    while (Math.abs(cof1) - Math.abs(cof2) >= -p) {
                        if (cof1 < 0 && cof2 < 0) {
                            cof1++
                            cof2--
                        } else if (cof1 >= 0 && cof2 >= 0) {
                            cof1--
                            cof2++
                        } else if (cof1 < 0 && cof2 >= 0) {
                            cof1++
                            cof2++
                        } else if (cof1 >= 0 && cof2 < 0) {
                            cof1--
                            cof2--
                        }
                    }
                }

                dctCof[coef1.x][coef1.y] = cof1
                dctCof[coef2.x][coef2.y] = cof2

                val colors = reverseDCT(dctCof)

                for (i in 0..colors.size-1) {
                    for (j in 0..colors[i].size-1) {
                        val blueColor = colors[i][j]
                        val pixel = bitmap.getPixel(y + i, x + j)
                        bitmap.setPixel(
                                y + i, x + j,
                                Color.argb(
                                        Color.alpha(pixel),
                                        Color.red(pixel),
                                        Color.green(pixel),
                                        blueColor))
                    }
                }

                byteBit--;
            }
        }

        val fOut = FileOutputStream(outFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        fOut.flush();
        fOut.close();
    }


    fun decode(file : File) : String {
        var bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val msgByte = mutableListOf<Byte>()
        var byte: Byte = 0;
        var byteBit = 7
        var msgSize = -1;

        loop@for (y in 0..bitmap.height - 1 step matrixSize) {
            for (x in 0..bitmap.width - 1 step matrixSize) {
                val arr = Array2dOfInt(matrixSize, matrixSize)

                for (i in 0..arr.size - 1) {
                    for (j in 0..arr[i].size - 1) {
                        val pixel = bitmap.getPixel(y + i, x + j)
                        arr[i][j] = Color.blue(pixel)
                    }
                }

                var dctCof = DCT(arr)

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

                if (Math.abs(dctCof[coef1.x][coef1.y]) > Math.abs(dctCof[coef2.x][coef2.y])) {
                    byte = byte.setZeroAtPos(byteBit)
                } else {
                    byte = byte.setOneAtPos(byteBit)
                }

                byteBit--;
            }
        }

        val msg = String(msgByte.toByteArray())
        Log.d(LOG_TAG, "Message: $msg")

        return msg
    }


    data class Coefficient (val x : Int, val y : Int);


}
