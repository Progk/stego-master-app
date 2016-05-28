package com.ssivulskiy.stegomaster.fragments.base

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TableLayout
import com.squareup.picasso.Picasso
import com.ssivulskiy.stegomaster.R
import com.ssivulskiy.stegomaster.core.base.BaseStegoAlgorithm
import com.ssivulskiy.stegomaster.core.base.BaseStegoLsbAlgorithm
import com.ssivulskiy.stegomaster.utils.*
import kotlinx.android.synthetic.main.fragment_lsb.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.px2dip
import java.io.File
import java.io.FileOutputStream

abstract  class BaseStegoFragment : Fragment() {

    protected val LOG_TAG = javaClass.simpleName

    protected  val SELECT_PHOTO_REQUEST = 100;

    protected  var mFileIn = "cars.jpg"

    protected var mFileOut = "stego"



    protected var mInImageUri : Uri? = null
    protected var mImageWidth = 0
    protected var mImageHight = 0




    protected @DrawableRes val mDefaultImage = R.drawable.cars

    protected lateinit var mStegoAlgorithm: BaseStegoAlgorithm

    protected var mMessage = "Android is a mobile operating system (OS) currently developed by Google, based on the Linux kernel and designed primarily for touchscreen mobile devices such as smartphones and tablets. Android's user interface is mainly based on direct manipulation, using touch gestures that loosely correspond to real-world actions, such as swiping, tapping and pinching, to manipulate on-screen objects, along with a virtual keyboard for text input. In addition to touchscreen devices, Google has further developed Android TV for televisions, Android Auto for cars, and Android Wear for wrist watches, each with a specialized user interface. Variants of Android are also used on notebooks, game consoles, digital cameras, and other electronics."
//    protected var mMessage = "abcdefghabcdefghabcdefgh"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_open -> {
                Log.i(LOG_TAG, "open")
                openImage()
                return true
            }
            R.id.action_save -> {
                Log.i(LOG_TAG, "save")
                saveImage("png")
                return true
            }
        }

        return false
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SELECT_PHOTO_REQUEST -> {
                    mInImageUri = data?.data
                    val bm = getBitmap()
                    mImageWidth = bm.width
                    mImageHight = bm.height
                    Log.i(LOG_TAG, "Width: $mImageWidth, Height $mImageHight")
                    if (!bm.isRecycled)
                        bm.recycle()
                    loadImage(mInImageUri!!)
                }
            }
        }
    }

    protected fun getBitmap() : Bitmap {
        val options = BitmapFactory.Options().apply {
            inMutable = true
        }
        val inputStream = activity.contentResolver.openInputStream(mInImageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        return bitmap
    }

    protected abstract fun calculateSecretImageLength() : Int;

    protected abstract fun loadImage(uri : Uri)

    protected fun saveImage(format : String) {
        val editText = EditText(activity)
        editText.setPadding(dpToPix(20), dpToPix(10), dpToPix(20), 0)
        editText.setBackgroundResource(android.R.color.transparent)
        val dialog = AlertDialog.Builder(activity).apply {
            setTitle("File Name:")
            setView(editText)
            setPositiveButton(android.R.string.ok) { dialog, which ->

            }
            setNegativeButton(android.R.string.cancel) { dialog, which ->

            }
        }
        val alertDialog = dialog.create()

        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (TextUtils.isEmpty(editText.text)) {
                editText.error = getString(R.string.input_text_msg)
                return@setOnClickListener
            } else {
                val fileout = File(context.cacheDir, mFileOut)
                copy(fileout, File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "${editText.text.toString()}.$format"))
                alertDialog.dismiss()
            }
        }
    }

    protected fun openImage() {
        val imageItenet = Intent(Intent.ACTION_PICK)
        imageItenet.type = "image/*"
        startActivityForResult(imageItenet, SELECT_PHOTO_REQUEST)
    }

}