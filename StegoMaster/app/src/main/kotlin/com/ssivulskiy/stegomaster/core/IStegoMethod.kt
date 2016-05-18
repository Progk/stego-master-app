package com.ssivulskiy.stegomaster.core

import java.io.File


interface IStegoMethod {

    fun code(msgByte : List<Byte>, inFile : File, outFile : File)

    fun decode(file : File) : List<Byte>

    fun decodeMsgSize(msg : List<Byte>) : Int
}