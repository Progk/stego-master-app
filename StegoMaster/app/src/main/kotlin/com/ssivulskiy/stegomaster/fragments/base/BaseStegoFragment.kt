package com.ssivulskiy.stegomaster.fragments.base

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import com.ssivulskiy.stegomaster.core.base.BaseStegoAlgorithm
import com.ssivulskiy.stegomaster.utils.*
import java.io.File
import java.io.FileOutputStream

open class BaseStegoFragment : Fragment() {

    protected val LOG_TAG = javaClass.simpleName

    protected  var mFileIn = "cars.jpg"

    protected var mFileOut = "cars_stego.jpeg"

    protected lateinit var mStegoAlgorithm: BaseStegoAlgorithm

    protected var mMessage = "Android is a mobile operating system (OS) currently developed by Google, based on the Linux kernel and designed primarily for touchscreen mobile devices such as smartphones and tablets. Android's user interface is mainly based on direct manipulation, using touch gestures that loosely correspond to real-world actions, such as swiping, tapping and pinching, to manipulate on-screen objects, along with a virtual keyboard for text input. In addition to touchscreen devices, Google has further developed Android TV for televisions, Android Auto for cars, and Android Wear for wrist watches, each with a specialized user interface. Variants of Android are also used on notebooks, game consoles, digital cameras, and other electronics."
//    protected var mMessage = "abcdefghabcdefghabcdefgh"

    protected fun calculateParam() {
        var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        dir = File(dir, "stego")
        val fileOut = File(dir, mFileOut)
        val fileIn = dir.listFiles().find { it.name.equals(mFileIn) }
        var emptyBitmap = BitmapFactory.decodeFile(fileIn!!.absolutePath)
        var msgBitmap = BitmapFactory.decodeFile(fileOut.absolutePath)

        val md = MD(emptyBitmap, msgBitmap)
        Log.i(LOG_TAG, "MD: $md")

        val ad = AD(emptyBitmap, msgBitmap)
        Log.i(LOG_TAG, "AD: $ad")

        val nad = NAD(emptyBitmap, msgBitmap)
        Log.i(LOG_TAG, "NAD: $nad")

        val mse = MSE(emptyBitmap, msgBitmap)
        Log.i(LOG_TAG, "MSE: $mse")

        val snr = SNR(emptyBitmap, msgBitmap)
        Log.i(LOG_TAG, "SNR: $snr")

        val iff = IF(emptyBitmap, msgBitmap)
        Log.i(LOG_TAG, "IF: $iff")

        val cq = NC(emptyBitmap, msgBitmap)
        Log.i(LOG_TAG, "NC: $cq")
    }


    protected fun calculateDiff() {
        var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        dir = File(dir, "stego")
        val fileOut = File(dir, mFileOut)
        val fileIn = dir.listFiles().find { it.name.equals(mFileIn) }

        var emptyBitmap = BitmapFactory.decodeFile(fileIn!!.absolutePath)
        var msgBitmap = BitmapFactory.decodeFile(fileOut.absolutePath)

        val diffBitmap = IMAGE_DIFF(emptyBitmap, msgBitmap, 128)

        val diffFile = File(fileOut.parent, fileOut.name + "_diff.png")
        val fOut = FileOutputStream(diffFile);
        diffBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();
    }

}