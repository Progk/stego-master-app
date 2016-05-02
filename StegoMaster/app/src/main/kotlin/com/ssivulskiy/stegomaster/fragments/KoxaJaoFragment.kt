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
import com.ssivulskiy.stegomaster.utils.*
import kotlinx.android.synthetic.main.fragment_stego.*
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.io.FileOutputStream

/**
 * @author Sivulskiy Sergey
 */
class KoxaJaoFragment : Fragment() {

    private val LOG_TAG = javaClass.name

    private val FILE_NAME_IN = "snowman.jpg"
    private val FILE_NAME_OUT = "snow_stego_koxa_jp.jpeg"


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

        val msg = decodeKoxaJao(fileIn!!)
        toast(msg)
        Log.d(LOG_TAG, "Message: $msg")
    }

    private fun codeButtonClick() {
        Log.d(LOG_TAG, "codeClick")
        var finish = false
        val msg = "hello world"
        val msgByte = (msg.length.toString() + msg).toByteArray()
        Log.d(LOG_TAG, msg.length.toString())
        Log.d(LOG_TAG, "Source data: ${msgByte.toList()}")
        var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        dir = File(dir, "stego")
        val fileIn = dir.listFiles().find { it.name.equals(FILE_NAME_IN) }
        val file = File(dir, FILE_NAME_OUT)
        val fOut = FileOutputStream(file);

        codeKoxaJao(fileIn!!, file, msg)
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


        Picasso.with(context).load(file).into(imageView)
    }


}
