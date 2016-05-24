package com.ssivulskiy.stegomaster.utils

import com.ssivulskiy.stegomaster.utils.*
import java.io.File


class PermutationByte {

    var mPermutationMatrix = arrayOf(
            arrayOf(0, 0, 0, 0, 0, 0, 0, 1),
            arrayOf(0, 0, 1, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 1, 0, 0),
            arrayOf(0, 1, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 1, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 1, 0),
            arrayOf(0, 0, 0, 0, 1, 0, 0, 0),
            arrayOf(1, 0, 0, 0, 0, 0, 0, 0)
    )

    private fun permutation(msg : List<Byte>) : List<Byte> {
        var permList = mutableListOf<Byte>()
        for (b in msg) {
            var permByte : Byte = 0
            for (i in 0..mPermutationMatrix.size - 1) {
                for (j in 0..mPermutationMatrix[i].size - 1) {
                    if (mPermutationMatrix[i][j] == 1) {
                        val sourceBit = b.getBitAtPos(i)
                        if (sourceBit == 1.toByte()) {
                            permByte = permByte.setOneAtPos(j)
                        } else {
                            permByte = permByte.setZeroAtPos(j)
                        }
                        break
                    }
                }
            }
            permList.add(permByte)
        }
        return permList
    }

    private fun permutationBack(msg : List<Byte>) : List<Byte> {
        var permList = mutableListOf<Byte>()
        for (b in msg) {
            var permByte : Byte = 0
            for (i in 0..mPermutationMatrix.size - 1) {
                for (j in 0..mPermutationMatrix[i].size - 1) {
                    if (mPermutationMatrix[j][i] == 1) {
                        val sourceBit = b.getBitAtPos(i)
                        if (sourceBit == 1.toByte()) {
                            permByte = permByte.setOneAtPos(j)
                        } else {
                            permByte = permByte.setZeroAtPos(j)
                        }
                        break
                    }
                }
            }
            permList.add(permByte)
        }
        return permList
    }

}
