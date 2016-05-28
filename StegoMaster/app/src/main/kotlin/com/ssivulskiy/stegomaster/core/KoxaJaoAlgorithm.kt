package com.ssivulskiy.stegomaster.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.ssivulskiy.stegomaster.core.base.BaseStegoAlgorithm
import com.ssivulskiy.stegomaster.utils.*
import java.io.File
import java.io.FileOutputStream

class KoxaJaoAlgorithm() : BaseStegoAlgorithm() {

    var mCoef1 = Coefficient(3, 4)
    var mCoef2 = Coefficient(4, 3)

    var P = 45

    var mMatrixSize = 8 //N

    var mComponent = Component.BLUE

    var mCompressFormat = Bitmap.CompressFormat.JPEG

    var mCompressQuality = 100

    override fun code(msgByte : List<Byte>, bitmap : Bitmap, outFile : File) {

    }

    override fun code(msgByte: List<Byte>, inFile: File, outFile: File) {
        val list = mutableListOf<Int>()
//        Log.i(LOG_TAG, msgByte.toString())
        val options = BitmapFactory.Options().apply {
            inMutable = true
        }
        var bitmap = BitmapFactory.decodeFile(inFile.absolutePath, options)

        var byte = 0;
        var byteBit = 7

        loop@for (y in 0..bitmap.height - 1 step mMatrixSize) {
            for (x in 0..bitmap.width - 1 step mMatrixSize) {
                val arr = Array2dOfInt(mMatrixSize, mMatrixSize)
                for (i in 0..arr.size - 1) {
                    for (j in 0..arr[i].size - 1) {
                        val pixel = bitmap.getPixel(x + j, y + i)
                        arr[i][j] = getStegoColor(pixel)
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

                var cof1 = Math.abs(dctCof[mCoef1.x][mCoef1.y])
                var cof2 = Math.abs(dctCof[mCoef2.x][mCoef2.y])

                var factor1 : Int
                var factor2 : Int

                if (dctCof[mCoef1.x][mCoef1.y] >= 0)
                    factor1 = 1
                else
                    factor1 = -1

                if (dctCof[mCoef2.x][mCoef2.y] >= 0)
                    factor2 = 1
                else
                    factor2 = -1

                if (cof1 - cof2 <= P && value.getBitAtPos(byteBit) == 0.toByte()) {
                    cof1 = P + cof2 + 1
                    list.add(0)
                } else if (cof1 - cof2 >= -P && value.getBitAtPos(byteBit) == 1.toByte()) {
                    cof2 = P + cof1 + 1
                    list.add(1)
                } else {
                    if (cof1 - cof2 > P)
                        list.add(0)
                    else if (cof1 - cof2 < -P)
                        list.add(1)
                }


                dctCof[mCoef1.x][mCoef1.y] = cof1 * factor1
                dctCof[mCoef2.x][mCoef2.y] = cof2 * factor2

//                Log.i(LOG_TAG, "C1: ${dctCof[mCoef1.x][mCoef1.y]} C2 :${dctCof[mCoef2.x][mCoef2.y]} Bit: ${value.getBitAtPos(byteBit)}")
//                if (Math.abs(dctCof[mCoef1.x][mCoef1.y]) > Math.abs(dctCof[mCoef2.x][mCoef2.y])) {
//                    Log.i(LOG_TAG, "0 true")
//                } else if (Math.abs(dctCof[mCoef1.x][mCoef1.y]) < Math.abs(dctCof[mCoef2.x][mCoef2.y])) {
//                    Log.i(LOG_TAG, "1 false")
//                }

                val stegoColors = normDCT(reverseDCT(dctCof))


                for (i in 0..stegoColors.size - 1) {
                    for (j in 0..stegoColors[i].size - 1) {
                        val stegoColor = stegoColors[i][j]
                        val sourcePixel = bitmap.getPixel(x + j, y + i)
                        var newPixel = createStegoPixel(sourcePixel, stegoColor)

                        if (sourcePixel != newPixel && mIsShowChangedPixels) {
                            newPixel = Color.BLACK
                        }

                        bitmap.setPixel(x + j, y + i, newPixel)
                    }
                }

                byteBit--;
            }
        }
//        Log.i(LOG_TAG, list.toString())

        val fOut = FileOutputStream(outFile);
        bitmap.compress(mCompressFormat, mCompressQuality, fOut);
        fOut.flush();
        fOut.close();
    }


    override fun decode(bitmap: Bitmap): List<Byte> {
        throw UnsupportedOperationException()
    }

    override fun decode(file: File): List<Byte> {
        var bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val msgByte = mutableListOf<Byte>()
        var byte: Byte = 0;
        var byteBit = 7
        var msgSize = -1;
        val list = mutableListOf<Int>()
        loop@for (y in 0..bitmap.height - 1 step mMatrixSize) {
            for (x in 0..bitmap.width - 1 step mMatrixSize) {
                val arr = Array2dOfInt(mMatrixSize, mMatrixSize)

                for (i in 0..arr.size - 1) {
                    for (j in 0..arr[i].size - 1) {
                        val pixel = bitmap.getPixel(x + j, y + i)
                        arr[i][j] = getStegoColor(pixel)
                    }
                }

                var dctCof = DCT(arr)

                if (byteBit == -1) {
                    msgByte.add(byte)
                    byte = 0
                    byteBit = 7
                    if (msgSize == -1 && msgByte.size == 2) {
                        msgSize = decodeMessageSize(msgByte)
                        msgByte.clear()
//                        Log.d(LOG_TAG, "Message size: $msgSize")
                    }

                }

                if (msgSize != -1 && msgSize == msgByte.size)
                    break@loop

                if (Math.abs(dctCof[mCoef1.x][mCoef1.y]) >= Math.abs(dctCof[mCoef2.x][mCoef2.y])) {
                    byte = byte.setZeroAtPos(byteBit)
                    list.add(0)
                } else {
                    byte = byte.setOneAtPos(byteBit)
                    list.add(1)
                }

                byteBit--;
            }
        }
        Log.i(LOG_TAG, list.toString())
        return msgByte
    }

    override fun decodeMessageSize(msg: List<Byte>): Int {
        Log.i(LOG_TAG, msg.toString())
        return calculateMessageLength(msg)
    }

    private fun createStegoPixel(sourcePixel: Int, stegoColor: Int): Int {
        when (mComponent) {
            Component.RED -> {
                return Color.argb(
                        Color.alpha(sourcePixel),
                        stegoColor,
                        Color.green(sourcePixel),
                        Color.blue(sourcePixel))
            }
            Component.BLUE -> {
                return Color.argb(
                        Color.alpha(sourcePixel),
                        Color.red(sourcePixel),
                        stegoColor,
                        Color.blue(sourcePixel))
            }
            Component.GREEN -> {
                return Color.argb(
                        Color.alpha(sourcePixel),
                        Color.red(sourcePixel),
                        Color.green(sourcePixel),
                        stegoColor)
            }
            else -> {
                throw RuntimeException("Component not supported")
            }
        }

    }

    private fun getStegoColor(pixel: Int): Int {
        when (mComponent) {
            Component.RED -> return Color.red(pixel)
            Component.GREEN -> return Color.green(pixel)
            Component.BLUE -> return Color.blue(pixel)
            else -> {
                throw RuntimeException("Component not supported")
            }
        }
    }


    data class Coefficient(val x: Int, val y: Int);

    enum class Component() {
        RED,
        GREEN,
        BLUE
    }
}
