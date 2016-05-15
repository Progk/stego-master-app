package com.ssivulskiy.stegomaster.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.ssivulskiy.stegomaster.R
import com.ssivulskiy.stegomaster.core.KoxaJaoMethod
import com.ssivulskiy.stegomaster.utils.*
import kotlinx.android.synthetic.main.fragment_koxa_jao.*
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.io.FileOutputStream

/**
 * @author Sivulskiy Sergey
 */
class KoxaJaoFragment : Fragment() {

    private val LOG_TAG = javaClass.name

    private val FILE_NAME_IN = "cars.jpg"
    private val FILE_NAME_OUT = "cars_stego_koxa_jp.jpeg"



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_koxa_jao, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        codeButton.setOnClickListener({
            codeButtonClick()
        })
        decodeButton.setOnClickListener({
            decodeButtonClick()
        })
    }

    private fun decodeButtonClick() {
        Log.d(LOG_TAG, "decodeClick")

        var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        dir = File(dir, "stego")

        val fileIn = dir.listFiles().find { it.name.equals(FILE_NAME_OUT) }
        val koxaJaoStego = KoxaJaoMethod()
        val msg = koxaJaoStego.decode(fileIn!!)
        val stringMsg = String(msg.toByteArray())
        Log.d(LOG_TAG, stringMsg)
        toast(stringMsg)
    }

    private fun codeButtonClick() {
//        val msg = "I've never been particularly fond of spin classes, as they eschew all the things I enjoy about using a stationary bike: The ability to set my own pace, listen to my music and maybe even dip into a good book while I pedal. But I can understand the appeal of a spin class, as the presence of an instructor can push you out of your comfort zone and ensure that you get a real workout. So it would seem that IMAXShift sits somewhere in the middle, combining an intense audio and visual experience to entertain you while a dedicated instructor gives orders. The problem is, there might have been just little too much going on for me to enjoy any one aspect to the fullest."
        val msg = "Android is a mobile operating system (OS) currently developed by Google, based on the Linux kernel and designed primarily for touchscreen mobile devices such as smartphones and tablets. Android's user interface is mainly based on direct manipulation, using touch gestures that loosely correspond to real-world actions, such as swiping, tapping and pinching, to manipulate on-screen objects, along with a virtual keyboard for text input. In addition to touchscreen devices, Google has further developed Android TV for televisions, Android Auto for cars, and Android Wear for wrist watches, each with a specialized user interface. Variants of Android are also used on notebooks, game consoles, digital cameras, and other electronics."

        var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        dir = File(dir, "stego")

        val fileIn = dir.listFiles().find { it.name.equals(FILE_NAME_IN) }
        val fileOut = File(dir, FILE_NAME_OUT)

        val koxaJaoStego = KoxaJaoMethod()


        koxaJaoStego.code(makeStegoMessage(msg), fileIn!!, fileOut)

//        val dcpCof = arrayOfNulls<IntArray>(8)
//
//        dcpCof[0] = intArrayOf(79, 75, 79, 82, 82, 86, 94, 94)
//        dcpCof[1] = intArrayOf(76, 78, 76, 82, 83, 86, 85, 94)
//        dcpCof[2] = intArrayOf(72, 75, 67, 78, 80, 78, 74, 82)
//        dcpCof[3] = intArrayOf(74, 76, 75, 75, 86, 80, 81, 79)
//        dcpCof[4] = intArrayOf(73, 70, 75, 67, 78, 78, 79, 85)
//        dcpCof[5] = intArrayOf(69, 63, 68, 69, 75, 78, 82, 80)
//        dcpCof[6] = intArrayOf(76, 76, 71, 71, 67, 79, 80, 83)
//        dcpCof[7] = intArrayOf(72, 77, 78, 69, 75, 75, 78, 78)


//        val source = dcpBack(cof)

        var emptyBitmap = BitmapFactory.decodeFile(fileIn.absolutePath)
        var msgBitmap = BitmapFactory.decodeFile(fileOut.absolutePath)

//        val md = MD(emptyBitmap, msgBitmap)
//        Log.i(LOG_TAG, "MD: $md")
//
//        val ad = AD(emptyBitmap, msgBitmap)
//        Log.i(LOG_TAG, "AD: $ad")
//
//        val nad = NAD(emptyBitmap, msgBitmap)
//        Log.i(LOG_TAG, "NAD: $nad")
//
//        val mse = MSE(emptyBitmap, msgBitmap)
//        Log.i(LOG_TAG, "MSE: $mse")
//
//        val snr = SNR(emptyBitmap, msgBitmap)
//        Log.i(LOG_TAG, "SNR: $snr")
//
//        val iff = IF(emptyBitmap, msgBitmap)
//        Log.i(LOG_TAG, "IF: $iff")
//
//        val cq = NC(emptyBitmap, msgBitmap)
//        Log.i(LOG_TAG, "NC: $cq")


        Picasso.with(context).load(fileOut).into(imageView)
    }

    companion object {
        fun newInstance() : KoxaJaoFragment {
            var args = Bundle()

            var fragment = KoxaJaoFragment()
            fragment.apply {
                arguments = args
            }

            return fragment
        }
    }


}
