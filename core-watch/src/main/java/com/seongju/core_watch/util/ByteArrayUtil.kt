package com.seongju.core_watch.util

fun intToByteArray(value: Int): ByteArray {
    val byteArray = ByteArray(4)

    byteArray[0] = (value shr 24 and 0xFF).toByte()
    byteArray[1] = (value shr 16 and 0xFF).toByte()
    byteArray[2] = (value shr 8 and 0xFF).toByte()
    byteArray[3] = (value and 0xFF).toByte()

    return byteArray
}

fun byteArrayToInt(byteArray: ByteArray): Int {
    require(byteArray.size >= 4) { "ByteArray size must be at least 4 bytes" }

    val value0 = (byteArray[0].toInt() and 0xFF) shl 24
    val value1 = (byteArray[1].toInt() and 0xFF) shl 16
    val value2 = (byteArray[2].toInt() and 0xFF) shl 8
    val value3 = byteArray[3].toInt() and 0xFF

    return value0 or value1 or value2 or value3
}

fun floatToByteArray(value: Float): ByteArray {
    val intValue = value.toRawBits()
    val byteArray = ByteArray(4)

    byteArray[0] = (intValue shr 24 and 0xFF).toByte()
    byteArray[1] = (intValue shr 16 and 0xFF).toByte()
    byteArray[2] = (intValue shr 8 and 0xFF).toByte()
    byteArray[3] = (intValue and 0xFF).toByte()

    return byteArray
}

fun byteArrayToFloat(byteArray: ByteArray): Float {
    require(byteArray.size >= 4) { "ByteArray size must be at least 4 bytes" }

    val intValue = (byteArray[0].toInt() and 0xFF shl 24) or
            (byteArray[1].toInt() and 0xFF shl 16) or
            (byteArray[2].toInt() and 0xFF shl 8) or
            (byteArray[3].toInt() and 0xFF)

    return Float.fromBits(intValue)
}