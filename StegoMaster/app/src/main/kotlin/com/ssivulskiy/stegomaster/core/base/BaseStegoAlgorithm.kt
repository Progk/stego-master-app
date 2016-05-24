package com.ssivulskiy.stegomaster.core.base

import android.util.Log
import com.ssivulskiy.stegomaster.utils.calculateMessageLength

abstract class BaseStegoAlgorithm : IStegoAlgorithm {

    protected val LOG_TAG = javaClass.simpleName

    var mIsShowChangedPixels = false

    override fun decodeMessageSize(msg: List<Byte>): Int {
        val messageSize = calculateMessageLength(msg)
        Log.i(LOG_TAG, "Message size: $messageSize")
        return messageSize
    }
}