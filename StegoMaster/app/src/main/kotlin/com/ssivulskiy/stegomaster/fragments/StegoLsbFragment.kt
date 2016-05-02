package com.ssivulskiy.stegomaster.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.squareup.picasso.Picasso

import com.ssivulskiy.stegomaster.R
import com.ssivulskiy.stegomaster.core.LSBStegoMethod
import com.ssivulskiy.stegomaster.utils.*
import kotlinx.android.synthetic.main.fragment_stego.*
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.io.FileOutputStream

/**
 * @author Sivulskiy Sergey
 */
class StegoLsbFragment : Fragment() {

    private val LOG_TAG = javaClass.name

    private val FILE_NAME_IN = "snowman.jpg"
    private val FILE_NAME_OUT = "snow_stego_lsb.png"


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_stego, container, false)
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
        val stegoLsbMethod = LSBStegoMethod()
        val msg = stegoLsbMethod.decode(fileIn!!)
        toast(msg)
    }

    private fun codeButtonClick() {
        val msg = "qwer"

        var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        dir = File(dir, "stego")

        val fileIn = dir.listFiles().find { it.name.equals(FILE_NAME_IN) }
        val fileOut = File(dir, FILE_NAME_OUT)

        val stegoLsbMethod = LSBStegoMethod()

        stegoLsbMethod.code(msg, fileIn!!, fileOut)

        Picasso.with(context).load(fileOut).into(imageView)
    }

    companion object {
        fun newInstance() : StegoLsbFragment {
            var args = Bundle()

            var fragment = StegoLsbFragment()
            fragment.apply {
                arguments = args
            }

            return fragment
        }
    }

}
