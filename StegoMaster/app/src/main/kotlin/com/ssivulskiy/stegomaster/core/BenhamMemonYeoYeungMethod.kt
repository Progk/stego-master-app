package com.ssivulskiy.stegomaster.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.ssivulskiy.stegomaster.utils.*
import java.io.File
import java.io.FileOutputStream

class BenhamMemonYeoYeungMethod() : IStegoMethod {

    private val LOG_TAG = javaClass.simpleName
    val MATRIX_SIZE = 8

    var mCoef1 = Coefficient(6, 2)
    var mCoef2 = Coefficient(4, 4)
    var mCoef3 = Coefficient(2, 6)

    var P = 60
    var Pl = 2600
    var Ph = 40


    var mComponent = Component.BLUE

    var mCompressFormat = Bitmap.CompressFormat.JPEG

    var mCompressQuality = 100

    fun countByteInImage(inFile : File) : Int {

        var bitmap = BitmapFactory.decodeFile(inFile.absolutePath)

        var countBit = 0
        loop@for (y in 0..bitmap.height - 1 step MATRIX_SIZE) {
            for (x in 0..bitmap.width - 1 step MATRIX_SIZE) {
                val arr = Array2dOfInt(MATRIX_SIZE, MATRIX_SIZE)
                for (i in 0..arr.size-1) {
                    for (j in 0..arr[i].size-1) {
                        val pixel = bitmap.getPixel(x + j, y + i)
                        arr[i][j] = getStegoColor(pixel)
                    }
                }

                val dctCof = DCT(arr)

                if (isCorrectBlock(dctCof))
                    countBit++
            }
        }

        return countBit / 8
    }

    override fun code(msgByte : List<Byte>, inFile : File, outFile : File) {
        val list = mutableListOf<Int>()
        Log.i(LOG_TAG, msgByte.toString())
        val options = BitmapFactory.Options().apply {
            inMutable = true
        }
        var bitmap = BitmapFactory.decodeFile(inFile.absolutePath, options)

        var byte = 0;
        var byteBit = 7

        loop@for (y in 0..bitmap.height - 1 step MATRIX_SIZE) {
            for (x in 0..bitmap.width - 1 step MATRIX_SIZE) {
                val arr = Array2dOfInt(MATRIX_SIZE, MATRIX_SIZE)
                for (i in 0..arr.size-1) {
                    for (j in 0..arr[i].size-1) {
                        val pixel = bitmap.getPixel(x + j, y + i)
                        arr[i][j] = getStegoColor(pixel)
                    }
                }

                var dctCof = DCT(arr)

                if (!isCorrectBlock(dctCof))
                    continue

                if (byteBit == -1) {
                    byte++;
                    byteBit = 7
                    if (byte == msgByte.size) {
                        break@loop
                    }
                }

                val value = msgByte[byte]

                var cof1 = dctCof[mCoef1.x][mCoef1.y]
                var cof2 = dctCof[mCoef2.x][mCoef2.y]
                var cof3 = dctCof[mCoef3.x][mCoef3.y]

                if (value.getBitAtPos(byteBit) == 0.toByte()) { //0
                   if (cof3 > cof1 || cof3 > cof2) {
                       val min = Math.min(cof1, cof2)
                       cof3 = min - P/2
                       if (cof1 == min)
                           cof1 += P/2
                       else
                           cof2 += P/2


                   }
                    list.add(0)
                    Log.i(LOG_TAG, "B:0 C1:$cof1 C2:$cof2 C3:$cof3")
                    Log.i(LOG_TAG, "${cof3 < Math.min(cof1, cof2)}")
                } else { //1
                    if (cof3 < cof1 || cof3 < cof2) {
                        val max = Math.max(cof1, cof2)
                        cof3 = max + P/2
                        if (cof1 == max)
                            cof1 -= P/2
                        else
                            cof2 -= P/2
                    }
                    list.add(1)
                    Log.i(LOG_TAG, "B:1 C1:$cof1 C2:$cof2 C3:$cof3")
                    Log.i(LOG_TAG, "${cof3 > Math.max(cof1, cof2)}")
                }


                dctCof[mCoef1.x][mCoef1.y] = cof1
                dctCof[mCoef2.x][mCoef2.y] = cof2
                dctCof[mCoef3.x][mCoef3.y] = cof3

                val stegoColors = reverseDCT(dctCof)

                var max = Int.MIN_VALUE
                for (i in 0..stegoColors.size-1) {
                    for (j in 0..stegoColors[i].size-1) {
                        if (stegoColors[i][j] > 255)
                            Log.i(LOG_TAG, "ddddddddd")
                    }
                }



                for (i in 0..stegoColors.size-1) {
                    for (j in 0..stegoColors[i].size-1) {
                        val stegoColor = stegoColors[i][j]
                        val sourcePixel = bitmap.getPixel(x + j, y + i)
                        var newPixel = createStegoPixel(sourcePixel, stegoColor)
                        bitmap.setPixel(x + j, y + i, newPixel)
                    }
                }

                byteBit--;
            }
        }
        Log.i(LOG_TAG, list.toString())
        val fOut = FileOutputStream(outFile);
        bitmap.compress(mCompressFormat, mCompressQuality, fOut);
        fOut.flush();
        fOut.close();
    }


    override fun decode(file : File) : List<Byte> {
        var bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val msgByte = mutableListOf<Byte>()
        var byte: Byte = 0;
        var byteBit = 7
        var msgSize = -1;
        val list = mutableListOf<Int>()
        loop@for (y in 0..bitmap.height - 1 step MATRIX_SIZE) {
            for (x in 0..bitmap.width - 1 step MATRIX_SIZE) {
                val arr = Array2dOfInt(MATRIX_SIZE, MATRIX_SIZE)

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
                        msgSize = decodeMsgSize(msgByte)
                        msgByte.clear()
                        Log.d(LOG_TAG, "Message size: $msgSize")
                    }

                }

                if (msgSize != -1 && msgSize == msgByte.size)
                    break@loop


                var cof1 = dctCof[mCoef1.x][mCoef1.y]
                var cof2 = dctCof[mCoef2.x][mCoef2.y]
                var cof3 = dctCof[mCoef3.x][mCoef3.y]

                if (cof3 < Math.min(cof1, cof2)) {
                    list.add(0)
                    byte = byte.setZeroAtPos(byteBit)
                } else {
                    list.add(1)
                    byte = byte.setOneAtPos(byteBit)
                }

                byteBit--;
            }
        }
        Log.i(LOG_TAG, list.toString())
        return msgByte
    }

    override fun decodeMsgSize(msg: List<Byte>): Int {
        Log.i(LOG_TAG, msg.toString())
        return calculateMessageLength(msg)
    }

    private fun createStegoPixel(sourcePixel : Int, stegoColor : Int) : Int {
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

    private fun isCorrectBlock(arr : Array<IntArray>) : Boolean {
        var x = MATRIX_SIZE - 2
        var y = MATRIX_SIZE - 2
        var value = 0
        for (i in 0..y) {
            for (j in 0..x) {
                value += arr[i][j]
            }
            x--;
        }
        if (value > Pl)
            return false

        y = 2
        value = 0
        for (i in arr.size - 1 downTo 2) {
            for (j in y..MATRIX_SIZE - 1) {
                value += arr[i][j]
            }
            y++
        }

        if (value < Ph)
            return false

        return true
    }

    private fun getStegoColor(pixel : Int) : Int {
        when (mComponent) {
            Component.RED -> return Color.red(pixel)
            Component.GREEN -> return Color.green(pixel)
            Component.BLUE -> return Color.blue(pixel)
            else -> {
                throw RuntimeException("Component not supported")
            }
        }
    }

    data class Coefficient (val x : Int, val y : Int);

    enum class Component() {
        RED,
        GREEN,
        BLUE
    }
}
