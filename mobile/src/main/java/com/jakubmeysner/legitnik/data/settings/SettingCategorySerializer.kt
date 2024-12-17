package com.jakubmeysner.legitnik.data.settings

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.jakubmeysner.legitnik.SettingCategory
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object SettingCategorySerializer : Serializer<SettingCategory> {
    override val defaultValue: SettingCategory = SettingCategory.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SettingCategory {
        try {
            return SettingCategory.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: SettingCategory, output: OutputStream) {
        t.writeTo(output)
    }
}
