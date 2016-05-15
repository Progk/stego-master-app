package com.ssivulskiy.stegomaster.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode


fun MD(emptyBitmap: Bitmap, msgBitmap: Bitmap) : Double {

    var max : Double = Double.MIN_VALUE

    for (i in 0..emptyBitmap.width - 1) {
        for (j in 0..emptyBitmap.height - 1) {
            val colorEmpty = emptyBitmap.getPixel(i, j).and(0x00FFFFFF)
            val coloMsg = msgBitmap.getPixel(i, j).and(0x00FFFFFF)

            max = Math.max(max, Math.abs(colorEmpty - coloMsg).toDouble())
        }
    }

    return max;
}

fun AD(emptyBitmap: Bitmap, msgBitmap: Bitmap) : Double {

    var sum : Double = 0.0

    for (i in 0..emptyBitmap.width - 1) {
        for (j in 0..emptyBitmap.height - 1) {
            val colorEmpty = emptyBitmap.getPixel(i, j).and(0x00FFFFFF)
            val coloMsg = msgBitmap.getPixel(i, j).and(0x00FFFFFF)

            sum += Math.abs(colorEmpty - coloMsg)
        }
    }

    return sum.div(emptyBitmap.width).div(emptyBitmap.height);
}

fun NAD(emptyBitmap: Bitmap, msgBitmap: Bitmap) : Double {

    var sumNum : Double = 0.0
    var sumDen : Double = 0.0

    for (i in 0..emptyBitmap.width - 1) {
        for (j in 0..emptyBitmap.height - 1) {
            val colorEmpty = emptyBitmap.getPixel(i, j).and(0x00FFFFFF)
            val coloMsg = msgBitmap.getPixel(i, j).and(0x00FFFFFF)

            sumNum += Math.abs(colorEmpty - coloMsg).toDouble()

            sumDen += Math.abs(colorEmpty).toDouble()
        }
    }

    return sumNum / sumDen;
}

fun MSE(emptyBitmap: Bitmap, msgBitmap: Bitmap) : Double {

    var sum : Double = 0.0

    for (i in 0..emptyBitmap.width - 1) {
        for (j in 0..emptyBitmap.height - 1) {
            val colorEmpty = emptyBitmap.getPixel(i, j).and(0x00FFFFFF).toDouble()
            val coloMsg = msgBitmap.getPixel(i, j).and(0x00FFFFFF).toDouble()

            sum += Math.pow(colorEmpty - coloMsg, 2.0)
        }
    }

    return sum.div(emptyBitmap.width).div(emptyBitmap.height);
}


fun SNR(emptyBitmap: Bitmap, msgBitmap: Bitmap) : Double {

    var sumNum = BigDecimal.ZERO
    var sumDen : Double = 0.0

    for (i in 0..emptyBitmap.width - 1) {
        for (j in 0..emptyBitmap.height - 1) {
            val colorEmpty = emptyBitmap.getPixel(i, j).and(0x00FFFFFF).toDouble()
            val coloMsg = msgBitmap.getPixel(i, j).and(0x00FFFFFF).toDouble()

            val cof = BigDecimal(colorEmpty).pow(2)
            sumNum += cof

            sumDen += Math.pow(colorEmpty - coloMsg, 2.0)
        }
    }

    return sumNum.divide(BigDecimal(sumDen), RoundingMode.HALF_DOWN).toDouble()
}

fun IF(emptyBitmap: Bitmap, msgBitmap: Bitmap) : Double {

    var sumNum : Double = 0.0
    var sumDen = BigDecimal.ZERO

    for (i in 0..emptyBitmap.width - 1) {
        for (j in 0..emptyBitmap.height - 1) {
            val colorEmpty = emptyBitmap.getPixel(i, j).and(0x00FFFFFF).toDouble()
            val coloMsg = msgBitmap.getPixel(i, j).and(0x00FFFFFF).toDouble()

            sumNum += Math.pow(colorEmpty - coloMsg, 2.0)

            val cof = BigDecimal(colorEmpty).pow(2)
            sumDen += cof
        }
    }

    return 1 - BigDecimal(sumNum).divide(sumDen, RoundingMode.HALF_DOWN).toDouble()
}


fun NC(emptyBitmap: Bitmap, msgBitmap: Bitmap) : Double {

    var sumNum = BigDecimal.ZERO
    var sumDen = BigDecimal.ZERO

    for (i in 0..emptyBitmap.width - 1) {
        for (j in 0..emptyBitmap.height - 1) {
            val colorEmpty = emptyBitmap.getPixel(i, j).and(0x00FFFFFF).toDouble()
            val coloMsg = msgBitmap.getPixel(i, j).and(0x00FFFFFF).toDouble()

            sumNum += BigDecimal(colorEmpty).multiply(BigDecimal(coloMsg))

            val cof = BigDecimal(colorEmpty).pow(2)
            sumDen += cof
        }
    }

    return sumNum.divide(sumDen, RoundingMode.HALF_UP).toDouble()
}