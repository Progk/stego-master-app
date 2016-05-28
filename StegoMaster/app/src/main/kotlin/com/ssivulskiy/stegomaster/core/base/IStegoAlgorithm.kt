package com.ssivulskiy.stegomaster.core.base

import android.graphics.Bitmap
import java.io.File


interface IStegoAlgorithm {

    fun code(msgByte : List<Byte>, inFile : File, outFile : File)

    fun code(msgByte : List<Byte>, bitmap : Bitmap, outFile : File)

    fun decode(file : File) : List<Byte>

    fun decode(bitmap: Bitmap) : List<Byte>

    fun decodeMessageSize(msg : List<Byte>) : Int
}