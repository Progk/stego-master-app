package com.ssivulskiy.stegomaster.utils

import android.graphics.BitmapFactory
import android.util.Log
import java.io.File

fun Array2dOfInt(i: Int, j: Int): Array<IntArray> = Array(i) { IntArray(j) }

fun makeStegoMessage(msg : String) : List<Byte> {

    val l = msg.length.and(0x0000FF00).shr(8).toByte()
    val r = msg.length.and(0x000000FF).toByte()

    val msgByte = mutableListOf(l, r)

    msg.toByteArray().forEach { msgByte.add(it) }

    return msgByte
}

fun calculateMessageLength(arr : List<Byte>) : Int = arr[0].toInt().and(0x000000FF).shl(8) or arr[1].toInt().and(0x000000FF)


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

fun normDCT(arr: Array<IntArray>): Array<IntArray> {
    var min = Int.MAX_VALUE
    var max = Int.MIN_VALUE

    for (i in 0..arr.size - 1) {
        for (j in 0..arr[i].size - 1) {
            if (arr[i][j] > max)
                max = arr[i][j]

            if (arr[i][j] < min) {
                min = arr[i][j]
            }
        }
    }

    val normArr = Array2dOfInt(arr.size, arr[0].size)

    for (i in 0..normArr.size - 1) {
        for (j in 0..normArr[i].size - 1) {
            normArr[i][j] = 255.times(arr[i][j] + Math.abs(min)).div(max + Math.abs(min)).and(0x000000FF)
        }
    }

    return normArr
}



