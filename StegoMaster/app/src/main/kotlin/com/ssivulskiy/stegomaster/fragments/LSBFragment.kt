package com.ssivulskiy.stegomaster.fragments

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.*
import com.squareup.picasso.Picasso
import com.ssivulskiy.stegomaster.R
import com.ssivulskiy.stegomaster.core.LSBAlgorithm
import com.ssivulskiy.stegomaster.fragments.base.BaseStegoFragment
import com.ssivulskiy.stegomaster.utils.bitCount
import com.ssivulskiy.stegomaster.utils.getBitAtPos
import com.ssivulskiy.stegomaster.utils.makeStegoMessage
import com.ssivulskiy.stegomaster.view.ColorPickerVIew
import kotlinx.android.synthetic.main.fragment_lsb.*
import org.jetbrains.anko.AlertDialogBuilder
import org.jetbrains.anko.async
import org.jetbrains.anko.onClick
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.progressDialog
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread
import org.xdty.preference.colorpicker.ColorPickerDialog
import org.xdty.preference.colorpicker.ColorPickerSwatch
import java.io.File

/**
 * @author Sivulskiy Sergey
 */
class LSBFragment : BaseStegoFragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_lsb, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mStegoAlgorithm = LSBAlgorithm()

        Picasso.with(context).load(mDefaultImage).into(imageView)

        select_colors_container.onClick {
            showColorDialog()
        }

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


    private fun showColorDialog() {
        val colorView = ColorPickerVIew(activity)
        val dialog = AlertDialog.Builder(activity).apply {
            setTitle(R.string.select_colors_component)
            setView(colorView)
            setPositiveButton(android.R.string.ok) { dialog, which ->

            }
        }
        val alertDialog = dialog.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (colorView.getSelectedColors() == 0) {
                toast(R.string.select_colors)
                return@setOnClickListener
            } else {
                getAlgorithm().mComponents = colorView.getSelectedColors()
                setColorVisible(circle_red, getAlgorithm().mComponents.getBitAtPos(2) == 1)
                setColorVisible(circle_green, getAlgorithm().mComponents.getBitAtPos(1) == 1)
                setColorVisible(circle_blue, getAlgorithm().mComponents.getBitAtPos(0) == 1)
                alertDialog.dismiss()
            }
        }

    }

    private fun setColorVisible(view : View, isVisible : Boolean) {
        if (isVisible)
            view.visibility = View.VISIBLE
        else
            view.visibility = View.GONE
    }


    private fun decodeButtonClick() {

        if (mInImageUri == null) {
            toast(R.string.select_image)
            return
        }

        var msg = emptyList<Byte>()
        val progress = indeterminateProgressDialog(getString(R.string.message_wait), getString(R.string.image_processing))
        async() {
            uiThread {
                progress.show()
            }
            try {
                msg = mStegoAlgorithm.decode(getBitmap())
            } catch (e : Exception) {
                uiThread {
                    toast(getString(R.string.can_not_decode_file))
                }
            } finally {
                uiThread {
                    progress.dismiss()
                }
            }
        }

        val stringMsg = String(msg.toByteArray())

        Log.d(LOG_TAG, stringMsg)

        toast(stringMsg)

        //calculate()
    }


    private fun codeButtonClick() {

        var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        if (mInImageUri == null) {
            toast(R.string.select_image)
            return
        }

        if (TextUtils.isEmpty(secret_message_text.text)) {
            secret_message_text.error = getString(R.string.input_text_msg)
            return
        }

        mMessage = secret_message_text.text.toString()

        val fileOut = File(context.cacheDir, mFileOut)


        val progress = indeterminateProgressDialog(getString(R.string.message_wait), getString(R.string.image_processing))
        async() {
            uiThread {
                progress.show()
            }
            try {
                getAlgorithm().code(makeStegoMessage(mMessage), getBitmap(), fileOut)
                uiThread {
                    Picasso.with(context).load(fileOut).into(imageView)
                }
            } catch (e : Exception) {
                uiThread {
                    toast(getString(R.string.can_not_decode_file))
                }
            } finally {
                uiThread {
                    progress.dismiss()
                }
            }
        }




    }

    fun getAlgorithm() : LSBAlgorithm {
        return mStegoAlgorithm as LSBAlgorithm
    }


    override fun calculateSecretImageLength() : Int {
        val bitInPixel = getAlgorithm().mComponents.bitCount()
        return mImageWidth * mImageHight / bitInPixel / 8 - 2 - 20

    }

    override fun loadImage(uri: Uri) {
        Picasso.with(context).load(uri).into(imageView)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SELECT_PHOTO_REQUEST -> {
                    val selectedImage = data?.data
                    val inputStream = activity.contentResolver.openInputStream(selectedImage)
                    Picasso.with(context).load(selectedImage).into(imageView)
                }
            }
        }
    }

    companion object {
        fun newInstance() : LSBFragment {
            val args = Bundle()

            val fragment = LSBFragment()
            fragment.apply {
                arguments = args
            }

            return fragment
        }
    }



}
