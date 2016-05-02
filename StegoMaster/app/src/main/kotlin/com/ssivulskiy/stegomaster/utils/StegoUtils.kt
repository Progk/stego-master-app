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
private val N = 8


fun codeTextLSB(inFile: File, outFile: File, msg: String) {
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
    loop@for (y in 0..bitmap.height - 1) {
        for (x in 0..bitmap.width - 1) {
            Log.d(LOG_TAG, "Encode pixel x:$x y:$y")
            var pixel = bitmap.getPixel(x, y)

            var alpha = Color.alpha(pixel)
            var red = Color.red(pixel)
            var green = Color.green(pixel)
            var blue = Color.blue(pixel)

            val colorArray = arrayOf(red, green, blue)

            for (colorItem in 0..colorArray.size - 1) {
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


fun decodeTextLSB(inFile: File): String {
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


fun codeKoxaJao(inFile: File, outFile: File, msg: String) {
    val x11 = 3
    val y11 = 4
    val x22 = 4
    val y22 = 3
    val p =  25

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
    loop@for (y in 0..bitmap.height - 1 step N) {
        for (x in 0..bitmap.width - 1 step N) {
            val arr = arrayOfNulls<IntArray>(N)
            for (x1 in 0..N-1) {
                arr[x1] = IntArray(N)
                for (y1 in 0..N-1) {
                    val pixel = bitmap.getPixel(x + x1, y + y1)
//                    arr[x1]!![y1] = Color.rgb(Color.red(pixel), Color.green(pixel), Color.blue(pixel))
                    arr[x1]!![y1] = pixel.and(0x000000FF)
                }
            }

            var dcpCof = dcp(arr)

            if (byteBit == -1) {
                byte++;
                byteBit = 7
                if (byte == msgByte.size) {
                    break@loop
                }
            }

            val value = msgByte[byte]
            var cof1 = dcpCof[x11]!![y11]
            var cof2 = dcpCof[x22]!![y22]

            if (value.getBitAtPos(byteBit) == 0.toByte()) { //0
                    while (Math.abs(cof1) - Math.abs(cof2) <= p) {
                        if (cof1 < 0 && cof2 < 0) {
                            cof1--
                            cof2++
                        } else if (cof1 >= 0 && cof2 >= 0) {
                            cof1++
                            cof2--
                        } else if (cof1 < 0 && cof2 >= 0) {
                            cof1--
                            cof2--
                        } else if (cof1 >= 0 && cof2 < 0) {
                            cof1++
                            cof2++
                        }
                    }
            } else { //1
                    while (Math.abs(cof1) - Math.abs(cof2) >= -p) {
                        if (cof1 < 0 && cof2 < 0) {
                            cof1++
                            cof2--
                        } else if (cof1 >= 0 && cof2 >= 0) {
                            cof1--
                            cof2++
                        } else if (cof1 < 0 && cof2 >= 0) {
                            cof1++
                            cof2++
                        } else if (cof1 >= 0 && cof2 < 0) {
                            cof1--
                            cof2--
                        }
                    }
            }

            dcpCof[x11]!![y11] = cof1
            dcpCof[x22]!![y22] = cof2
            dcpCof = dcpBack(dcpCof)

            for (x1 in 0..N-1) {
                for (y1 in 0..N-1) {
                    val bluePixel = dcpCof[x1]!![y1]
                    val pixel = bitmap.getPixel(x + x1, y + y1)
                    bitmap.setPixel(x + x1, y + y1,
                            Color.argb(Color.alpha(pixel), Color.red(pixel), Color.green(pixel), bluePixel))
                }
            }

            byteBit--;
        }
    }

    val fOut = FileOutputStream(outFile);
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
    fOut.flush();
    fOut.close();
}

fun decodeKoxaJao(inFile: File): String {
    val x11 = 3
    val y11 = 4
    val x22 = 4
    val y22 = 3
    val p =  25
    Log.d(LOG_TAG, "Start decode text")
    var bitmap = BitmapFactory.decodeFile(inFile!!.path)
    val msgByte = mutableListOf<Byte>()
    var byte: Byte = 0;
    var byteBit = 7
    var msgSize = -1;
    loop@for (y in 0..bitmap.height - 1 step N) {
        for (x in 0..bitmap.width - 1 step N) {
            val arr = arrayOfNulls<IntArray>(N)
            for (x1 in 0..N-1) {
                arr[x1] = IntArray(N)
                for (y1 in 0..N-1) {
                    val pixel = bitmap.getPixel(x + x1, y + y1)
                    arr[x1]!![y1] = pixel.and(0x000000FF)
                }
            }

            var dcpCof = dcp(arr)

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

            if (Math.abs(dcpCof[x11]!![y11]) > Math.abs(dcpCof[x22]!![y22])) {
                Log.i(LOG_TAG, 0.toString())
                byte = byte.setZeroAtPos(byteBit)
            } else {
                Log.i(LOG_TAG, 1.toString())
                byte = byte.setOneAtPos(byteBit)
            }

            byteBit--;
        }
    }
    val msg = String(msgByte.toByteArray())
    Log.d(LOG_TAG, "Message: $msg")

    return msg
}

fun dcp(arr: Array<IntArray?>): Array<IntArray?> {
    var dcpCof = arrayOfNulls<IntArray>(arr.size)
    for (i in 0..arr.size - 1) {
        dcpCof[i] = IntArray(arr[i]!!.size)
        for (j in 0..arr[i]!!.size - 1) {
            var temp: Double = 0.0
            for (x in 0..arr.size - 1) {
                for (y in 0..arr[x]!!.size - 1) {
                    temp += arr[x]!![y]
                            .times(Math.cos(((2 * x + 1) * i * Math.PI).div(2 * arr.size)))
                            .times(Math.cos(((2 * y + 1) * j * Math.PI).div(2 * arr.size)))
                }
            }
            if (j == 0)
                temp /= Math.sqrt(2.0)

            if (i == 0)
                temp /= Math.sqrt(2.0)

            temp /= Math.sqrt(2.0 * arr.size)

            dcpCof[i]!![j] = temp.toInt();
        }

    }
    return dcpCof;
}

fun dcpBack(dcpCof: Array<IntArray?>): Array<IntArray?> {
    var arr = arrayOfNulls<IntArray>(dcpCof.size)
    for (x in 0..arr.size - 1) {
        arr[x] = IntArray(dcpCof[x]!!.size)
        for (y in 0..dcpCof[x]!!.size - 1) {
            var sum: Double = 0.0
            for (i in 0..dcpCof.size - 1) {
                for (j in 0..dcpCof[i]!!.size - 1) {
                    var temp = 1.0

                    if (i == 0)
                        temp /= Math.sqrt(2.0);

                    if (j == 0)
                        temp /= Math.sqrt(2.0);


                    temp *= dcpCof[i]!![j]

                    temp *= Math.cos(((2 * x + 1) * i * Math.PI).div(2 * dcpCof.size))
                    temp *= Math.cos(((2 * y + 1) * j * Math.PI).div(2 * dcpCof.size))

                    sum += temp
                }

            }

            sum /= Math.sqrt(2.0 * dcpCof.size)

            arr[x]!![y] = sum.toInt()
        }

    }
    return arr;
}



