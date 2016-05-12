package com.ssivulskiy.stegomaster.utils

fun Int.getBitAtPos(pos : Int) : Int {
    return this.shr(pos).and(1)
}

fun Int.bitCount() : Int {

    var count = 0
    for (i in 0..7) {
        count += this.getBitAtPos(i)
    }

    return count
}