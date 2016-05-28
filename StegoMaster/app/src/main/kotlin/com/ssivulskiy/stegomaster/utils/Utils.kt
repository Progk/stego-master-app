package com.ssivulskiy.stegomaster.utils

import android.content.res.Resources
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

fun dpToPix(px : Int) : Int {
    return px.times(Resources.getSystem().displayMetrics.density).toInt()
}

fun copy(src : File, dst : File) {
    val ins = FileInputStream(src);
    val outs = FileOutputStream(dst);

    // Transfer bytes from in to out
    val buf = ByteArray(1024)
    var len : Int = 0
    len = ins.read(buf)
    while (len > 0) {
        outs.write(buf, 0, len);
        len = ins.read(buf)
    }
    ins.close();
    outs.close();
}