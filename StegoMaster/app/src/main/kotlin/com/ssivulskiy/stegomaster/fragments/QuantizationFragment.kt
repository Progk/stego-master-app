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
import com.ssivulskiy.stegomaster.core.QuantizationAlgorithm
import com.ssivulskiy.stegomaster.fragments.base.BaseStegoFragment
import com.ssivulskiy.stegomaster.utils.*
import kotlinx.android.synthetic.main.fragment_quantization.*
import org.jetbrains.anko.support.v4.toast
import java.io.File



class QuantizationFragment : BaseStegoFragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_quantization, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mStegoAlgorithm = QuantizationAlgorithm()
        mFileOut = "cars_quant.png"

        codeButton.setOnClickListener({
//            codeButtonClick()
            params()
        })
        decodeButton.setOnClickListener({
            decodeButtonClick()
        })
    }

    private fun params() {

        mStegoAlgorithm.mIsShowChangedPixels = false
        mFileOut = "cars_quant.png"
        codeButtonClick()
        calculateParam()
        mStegoAlgorithm.mIsShowChangedPixels = true
        mFileOut = "cars_quant_black.png"
        codeButtonClick()


    }

    private fun decodeButtonClick() {
        Log.d(LOG_TAG, "decodeClick")

        var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        dir = File(dir, "stego")

        val fileIn = dir.listFiles().find { it.name.equals(mFileOut) }

        val msg = mStegoAlgorithm.decode(fileIn!!)

        val stringMsg = String(msg.toByteArray())

        Log.d(LOG_TAG, stringMsg)

        toast(stringMsg)
    }

    private fun codeButtonClick() {
//
        var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        dir = File(dir, "stego")

        val fileIn = dir.listFiles().find { it.name.equals(mFileIn) }
        val fileOut = File(dir, mFileOut)


        mStegoAlgorithm.code(makeStegoMessage(mMessage), fileIn!!, fileOut)

        Picasso.with(context).load(fileOut).into(imageView)

    }


    companion object {
        fun newInstance() : QuantizationFragment {
            var args = Bundle()

            var fragment = QuantizationFragment()
            fragment.apply {
                arguments = args
            }

            return fragment
        }
    }

}
