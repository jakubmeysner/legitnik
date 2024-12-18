package com.jakubmeysner.legitnik.data.sdcatcard.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromByteList(byteList: List<Byte>): ByteArray {
        return byteList.toByteArray()
    }

    @TypeConverter
    fun toByteList(byteArray: ByteArray): List<Byte> {
        return byteArray.toList()
    }
}
