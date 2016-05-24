package com.ssivulskiy.stegomaster.fragments

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.ssivulskiy.stegomaster.R
import com.ssivulskiy.stegomaster.core.LSBAlgorithm
import com.ssivulskiy.stegomaster.fragments.base.BaseStegoFragment
import com.ssivulskiy.stegomaster.utils.makeStegoMessage
import kotlinx.android.synthetic.main.fragment_lsb_permutation.*
import org.jetbrains.anko.support.v4.toast
import java.io.File

/**
 * @author Sivulskiy Sergey
 */
class LSBFragment : BaseStegoFragment() {





    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_lsb, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mStegoAlgorithm = LSBAlgorithm()
        mFileOut = "cars_lsb_simple.png"

        codeButton.setOnClickListener({
            codeButtonClick()
//             params()
        })
        decodeButton.setOnClickListener({
            decodeButtonClick()
        })
    }

    private fun params() {

        mStegoAlgorithm.mIsShowChangedPixels = false
        mFileOut = "cars_lsb_simple.png"
        codeButtonClick()
        calculateParam()
        mStegoAlgorithm.mIsShowChangedPixels = true
        mFileOut = "cars_lsb_simple_black.png"
        codeButtonClick()


    }

    private fun decodeButtonClick() {
        var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        dir = File(dir, "stego")

        val fileIn = dir.listFiles().find { it.name.equals(mFileOut) }

        val msg = mStegoAlgorithm.decode(fileIn!!)

        val stringMsg = String(msg.toByteArray())

        Log.d(LOG_TAG, stringMsg)

        toast(stringMsg)

        //calculate()
    }


    private fun codeButtonClick() {

        var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        dir = File(dir, "stego")

        val fileIn = dir.listFiles().find { it.name.equals(mFileIn) }
        val fileOut = File(dir, mFileOut)


        mStegoAlgorithm.code(makeStegoMessage(mMessage), fileIn!!, fileOut)

        Picasso.with(context).load(fileOut).into(imageView)


    }

    companion object {
        fun newInstance() : LSBFragment {
            var args = Bundle()

            var fragment = LSBFragment()
            fragment.apply {
                arguments = args
            }

            return fragment
        }
    }

}
