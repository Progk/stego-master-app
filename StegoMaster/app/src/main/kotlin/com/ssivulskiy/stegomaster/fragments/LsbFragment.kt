package com.ssivulskiy.stegomaster.fragments

import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.ssivulskiy.stegomaster.R
import com.ssivulskiy.stegomaster.core.LSBStegoMethod
import kotlinx.android.synthetic.main.fragment_stego.*
import org.jetbrains.anko.support.v4.toast
import java.io.File

/**
 * @author Sivulskiy Sergey
 */
class LsbFragment : Fragment() {

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
//        val msg = "qwer"
        val msg = "I've never been particularly fond of spin classes, as they eschew all the things I enjoy about using a stationary bike: The ability to set my own pace, listen to my music and maybe even dip into a good book while I pedal. But I can understand the appeal of a spin class, as the presence of an instructor can push you out of your comfort zone and ensure that you get a real workout. So it would seem that IMAXShift sits somewhere in the middle, combining an intense audio and visual experience to entertain you while a dedicated instructor gives orders. The problem is, there might have been just little too much going on for me to enjoy any one aspect to the fullest."

        var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        dir = File(dir, "stego")

        val fileIn = dir.listFiles().find { it.name.equals(FILE_NAME_IN) }
        val fileOut = File(dir, FILE_NAME_OUT)

        val stegoLsbMethod = LSBStegoMethod()

        stegoLsbMethod.code(msg, fileIn!!, fileOut)

        Picasso.with(context).load(fileOut).into(imageView)
    }

    companion object {
        fun newInstance() : LsbFragment {
            var args = Bundle()

            var fragment = LsbFragment()
            fragment.apply {
                arguments = args
            }

            return fragment
        }
    }

}
