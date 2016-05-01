package com.ssivulskiy.stegomaster.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Environment
import android.util.Log
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_stego.*
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.io.FileOutputStream

private val LOG_TAG = "StegoUtils"


fun codeTextLSB(inFile : File, outFile : File, msg : String) {
    Log.d(LOG_TAG, "Start code text")
    val lengthBinary = String.format("%16s", Integer.toBinaryString(msg.length)).replace(' ', '0')
    val l1 = java.lang.Byte.parseByte(lengthBinary.substring(0, 8), 2)
    val l2 = java.lang.Byte.parseByte(lengthBinary.substring(8, 16), 2)
    val msgByte = mutableListOf(l1, l2)
    msg.toByteArray().forEach { msgByte.add(it) }

    Log.d(LOG_TAG, "Source data: ${msgByte.toList()}")

    val options = BitmapFactory.Options()
    options.inMutable = true;
    var bitmap = BitmapFactory.decodeFile(inFile!!.path, options)

    var byte = 0;
    var byteBit = 7
    var finish = false
    loop@for (y in 0..bitmap.height-1) {
        for (x in 0..bitmap.width-1) {
            Log.d(LOG_TAG,"Encode pixel x:$x y:$y")
            var pixel = bitmap.getPixel(x, y)

            var alpha = Color.alpha(pixel)
            var red = Color.red(pixel)
            var green = Color.green(pixel)
            var blue = Color.blue(pixel)

            val colorArray = arrayOf(red, green, blue)

            for (colorItem in 0..colorArray.size-1) {
                if (byteBit == -1) {
                    byte++;
                    byteBit = 7
                    if (byte == msgByte.size) {
                        finish = true
                        break
                    }
                }
                val value = msgByte[byte].getBitAtPos(byteBit).toInt()
                var color = colorArray[colorItem]
                Log.i(LOG_TAG, value.toString())
                if (value == 1) {
                    color = color or 1
                } else {
                    color = color and 1.inv()
                }
                colorArray[colorItem] = color
                byteBit--
            }

            red = colorArray[0]
            green = colorArray[1]
            blue = colorArray[2]

            val newPixel = Color.argb(alpha, red, green, blue)

            bitmap.setPixel(x, y, newPixel)

            if (finish)
                break@loop
        }
    }

    val fOut = FileOutputStream(outFile);
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
    fOut.flush();
    fOut.close();
}


fun decodeTextLSB(inFile : File) : String {
    Log.d(LOG_TAG, "Start decode text")
    var bitmap = BitmapFactory.decodeFile(inFile!!.path)
    val msgByte = mutableListOf<Byte>()
    var byte: Byte = 0;
    var byteBit = 7
    var msgSize = -1;
    loop@for (y in 0..bitmap.height - 1) {
        for (x in 0..bitmap.width - 1) {
            Log.d(LOG_TAG, "Decode pixel x:$x y:$y")
            var pixel = bitmap.getPixel(x, y)

            var alpha = Color.alpha(pixel)
            var red = Color.red(pixel)
            var green = Color.green(pixel)
            var blue = Color.blue(pixel)

            val colorArray = arrayOf(red, green, blue)

            for (colorItem in 0..colorArray.size - 1) {
                if (byteBit == -1) {
                    msgByte.add(byte)
                    byte = 0
                    byteBit = 7
                    if (msgSize == -1 && msgByte.size == 2) {
                        msgSize = msgByte[0].toInt().shl(8).or(msgByte[1].toInt())
                        msgByte.clear()
                        Log.d(LOG_TAG, "Msg size: $msgSize")
                    }

                }

                if (msgSize != -1 && msgSize == msgByte.size)
                    break@loop

                if (colorArray[colorItem].toByte().getBitAtPos(0).toInt() == 0) {
                    Log.i(LOG_TAG, 0.toString())
                    byte = byte.setZeroAtPos(byteBit)
                } else {
                    Log.i(LOG_TAG, 1.toString())
                    byte = byte.setOneAtPos(byteBit)
                }
                byteBit--;
            }

        }
    }
    val msg = String(msgByte.toByteArray())
    Log.d(LOG_TAG, "Message: $msg")

    return msg
}
