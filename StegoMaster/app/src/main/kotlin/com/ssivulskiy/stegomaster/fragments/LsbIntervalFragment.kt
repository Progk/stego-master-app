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
import com.ssivulskiy.stegomaster.core.LSBIntervalMethod
import com.ssivulskiy.stegomaster.core.LSBMethod
import com.ssivulskiy.stegomaster.utils.*
import kotlinx.android.synthetic.main.fragment_lsb_interval.*
import org.jetbrains.anko.support.v4.toast
import java.io.File

/**
 * @author Sivulskiy Sergey
 */
class LSBIntervalFragment : Fragment() {

    private val LOG_TAG = javaClass.name

    private val FILE_NAME_IN = "cars.jpg"
    private val FILE_NAME_OUT = "cars_stego_lsb_interval.png"


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_lsb, container, false)
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
        val stegoLsbMethod = LSBIntervalMethod()
        val msg = stegoLsbMethod.decode(fileIn!!)
        val stringMsg = String(msg.toByteArray())
        Log.d(LOG_TAG, stringMsg)
        toast(stringMsg)
        //calculate()
    }

    private fun codeButtonClick() {
//        val msg = "12"
//        val msg = "ITMO University is a large state university in Saint Petersburg and is one of Russias National Research Universities. ITMO University is one of 15 Russian universities that were selected to participate in Russian Academic Excellence Project 5100 by the government of the Russian Federation to improve their international competitiveness among the world’s leading research and educational"
        val msg = "Android is a mobile operating system (OS) currently developed by Google, based on the Linux kernel and designed primarily for touchscreen mobile devices such as smartphones and tablets. Android's user interface is mainly based on direct manipulation, using touch gestures that loosely correspond to real-world actions, such as swiping, tapping and pinching, to manipulate on-screen objects, along with a virtual keyboard for text input. In addition to touchscreen devices, Google has further developed Android TV for televisions, Android Auto for cars, and Android Wear for wrist watches, each with a specialized user interface. Variants of Android are also used on notebooks, game consoles, digital cameras, and other electronics."
        var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        dir = File(dir, "stego")

        val fileIn = dir.listFiles().find { it.name.equals(FILE_NAME_IN) }
        val fileOut = File(dir, FILE_NAME_OUT)

        val stegoLsbMethod = LSBIntervalMethod()

        stegoLsbMethod.code(makeStegoMessage(msg), fileIn!!, fileOut)

        Picasso.with(context).load(fileOut).into(imageView)

    }

    fun calculate() {
        var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        dir = File(dir, "stego")
        val fileOut = File(dir, FILE_NAME_OUT)
        val fileIn = dir.listFiles().find { it.name.equals(FILE_NAME_IN) }
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

    companion object {
        fun newInstance() : LSBIntervalFragment {
            var args = Bundle()

            var fragment = LSBIntervalFragment()
            fragment.apply {
                arguments = args
            }

            return fragment
        }
    }

}
