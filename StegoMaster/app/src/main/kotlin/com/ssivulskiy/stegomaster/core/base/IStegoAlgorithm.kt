package com.ssivulskiy.stegomaster.core.base

import java.io.File


interface IStegoAlgorithm {

    fun code(msgByte : List<Byte>, inFile : File, outFile : File)

    fun decode(file : File) : List<Byte>

    fun decodeMessageSize(msg : List<Byte>) : Int
}