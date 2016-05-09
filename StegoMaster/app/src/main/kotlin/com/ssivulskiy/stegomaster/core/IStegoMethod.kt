package com.ssivulskiy.stegomaster.core

import java.io.File

/**
 * @author Sivulskiy Sergey
 */
interface IStegoMethod {

    fun code(msg : List<Byte>, inFile : File, outFile : File)

    fun decode(file : File) : List<Byte>

    fun decodeMsgSize(msg : List<Byte>) : Int
}