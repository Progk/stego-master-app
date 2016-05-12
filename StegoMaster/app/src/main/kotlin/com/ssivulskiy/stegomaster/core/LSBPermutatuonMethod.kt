package com.ssivulskiy.stegomaster.core

import com.ssivulskiy.stegomaster.utils.*
import java.io.File


class LSBPermutatuonMethod : LSBStegoMethod() {

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


    override fun code(msgByte: List<Byte>, inFile: File, outFile: File) {
        val permutationByteList = permutation(msgByte)
        super.code(permutationByteList, inFile, outFile)
    }

    override fun decode(file: File): List<Byte> {
        val permMsg = super.decode(file)
        return permutationBack(permMsg)
    }

    override fun decodeMsgSize(msg: List<Byte>): Int {
        val decodedMsg = permutationBack(msg)
        return calculateMessageLength(decodedMsg)
    }

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
