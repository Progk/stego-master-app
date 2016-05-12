package com.ssivulskiy.stegomaster.utils

fun Byte.setOneAtPos(pos : Int) : Byte {
    return (this.toInt() or 1.shl(pos)).toByte()
}

fun Byte.setZeroAtPos(pos : Int) : Byte {
    return (this.toInt() and 1.shl(pos).inv()).toByte()
}

fun Byte.getBitAtPos(pos : Int) : Byte {
    return (this.toInt().shr(pos).and(1)).toByte()
}

