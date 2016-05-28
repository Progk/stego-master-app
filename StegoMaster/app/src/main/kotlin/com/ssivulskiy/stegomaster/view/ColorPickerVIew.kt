package com.ssivulskiy.stegomaster.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.ssivulskiy.stegomaster.R
import com.ssivulskiy.stegomaster.utils.setOneAtPos
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater

class ColorPickerVIew(mContext : Context) : FrameLayout(mContext) {

    private val LOG_TAG = javaClass.simpleName

    lateinit var mColor1 : View

    lateinit var mColor2 : View

    lateinit var mColor3 : View

    var mSelectedColor : Int = 0
    var mUnSelectedColor : Int = 0



    init {

        val view = context.layoutInflater.inflate(R.layout.dialog_color_items, null, false)

        mSelectedColor = resources.getColor(R.color.colorSelected)
        mUnSelectedColor = resources.getColor(R.color.colorNotSelected)

        mColor1 = view.find(R.id.color1)
        mColor2 = view.find(R.id.color2)
        mColor3 = view.find(R.id.color3)


        mColor1.backgroundColor = mUnSelectedColor
        mColor2.backgroundColor = mUnSelectedColor
        mColor3.backgroundColor = mUnSelectedColor

        mColor1.setOnClickListener {
            Log.i(LOG_TAG, "Color1")
            selectColor(mColor1)
        }

        mColor2.setOnClickListener {
            Log.i(LOG_TAG, "Color2")
            selectColor(mColor2)
        }

        mColor3.setOnClickListener {
            Log.i(LOG_TAG, "Color3")
            selectColor(mColor3)
        }

        addView(view)

    }


    private fun selectColor(view : View) {
        if ((view.background as ColorDrawable).color == mUnSelectedColor)
            view.setBackgroundColor(mSelectedColor)
        else
            view.setBackgroundColor(mUnSelectedColor)
    }

    fun getSelectedColors() : Int {
        var selected = 0
        if ((mColor1.background as ColorDrawable).color == mSelectedColor) {
            selected = selected.setOneAtPos(2)
        }

        if ((mColor2.background as ColorDrawable).color == mSelectedColor) {
            selected = selected.setOneAtPos(1)
        }

        if ((mColor3.background as ColorDrawable).color == mSelectedColor) {
            selected = selected.setOneAtPos(0)
        }

        return selected

    }


}