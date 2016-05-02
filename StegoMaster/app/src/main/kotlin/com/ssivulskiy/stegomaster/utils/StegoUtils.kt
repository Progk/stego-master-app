package com.ssivulskiy.stegomaster.utils

import android.graphics.BitmapFactory
import android.util.Log
import java.io.File

fun Array2dOfInt(i: Int, j: Int): Array<IntArray> = Array(i) { IntArray(j) }

fun prepareMessage(msg : String) : List<Byte> {

    val lengthBinary = String.format("%16s", Integer.toBinaryString(msg.length)).replace(' ', '0')

    val l1 = java.lang.Byte.parseByte(lengthBinary.substring(0, 8), 2)
    val l2 = java.lang.Byte.parseByte(lengthBinary.substring(8, 16), 2)

    val msgByte = mutableListOf(l1, l2)

    msg.toByteArray().forEach { msgByte.add(it) }

    return msgByte
}

fun DCT(arr: Array<IntArray>): Array<IntArray> {
    var dctCof = Array2dOfInt(arr.size, arr.size)

    for (i in 0..arr.size - 1) {
        for (j in 0..arr[i].size - 1) {
            var temp: Double = 0.0

            for (x in 0..arr.size - 1) {
                for (y in 0..arr[x].size - 1) {
                    temp += arr[x][y]
                            .times(Math.cos(((2 * x + 1) * i * Math.PI).div(2 * arr.size)))
                            .times(Math.cos(((2 * y + 1) * j * Math.PI).div(2 * arr.size)))
                }
            }

            if (j == 0)
                temp /= Math.sqrt(2.0)

            if (i == 0)
                temp /= Math.sqrt(2.0)

            temp /= Math.sqrt(2.0 * arr.size)

            dctCof[i][j] = temp.toInt();
        }

    }

    return dctCof;
}

fun reverseDCT(dcpCof: Array<IntArray>): Array<IntArray> {
    var arr = Array2dOfInt(dcpCof.size, dcpCof.size)
    for (x in 0..arr.size - 1) {
        for (y in 0..dcpCof[x].size - 1) {

            var sum: Double = 0.0
            for (i in 0..dcpCof.size - 1) {
                for (j in 0..dcpCof[i].size - 1) {
                    var temp = 1.0

                    if (i == 0)
                        temp /= Math.sqrt(2.0);

                    if (j == 0)
                        temp /= Math.sqrt(2.0);

                    temp *= dcpCof[i][j]

                    temp *= Math.cos(((2 * x + 1) * i * Math.PI).div(2 * dcpCof.size))
                    temp *= Math.cos(((2 * y + 1) * j * Math.PI).div(2 * dcpCof.size))

                    sum += temp
                }

            }

            sum /= Math.sqrt(2.0 * dcpCof.size)

            arr[x][y] = sum.toInt()
        }

    }
    return arr;
}



