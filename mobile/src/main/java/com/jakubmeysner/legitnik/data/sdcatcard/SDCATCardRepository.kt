package com.jakubmeysner.legitnik.data.sdcatcard

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.jakubmeysner.legitnik.domain.sdcatcard.toParsed
import java.util.UUID
import javax.inject.Inject

@Entity
data class SDCATCardRaw(
    @PrimaryKey val uuid: UUID,
    val type: SDCATCardType,
    @ColumnInfo("raw_message") val rawMessage: List<Byte>,
    @ColumnInfo("raw_certificate") val rawCertificate: List<Byte>,
)

class ByteListConverter {
    @TypeConverter
    fun fromByteList(byteList: List<Byte>): ByteArray {
        return byteList.toByteArray()
    }

    @TypeConverter
    fun toByteList(byteArray: ByteArray): List<Byte> {
        return byteArray.toList()
    }
}

@Dao
interface SDCATCardRawDao {
    @Query("SELECT * FROM SDCATCardRaw")
    suspend fun getAll(): List<SDCATCardRaw>

    @Query("SELECT * FROM SDCATCardRaw WHERE uuid = :uuid")
    suspend fun getOne(uuid: UUID): SDCATCardRaw

    @Insert
    suspend fun insert(card: SDCATCardRaw)

    @Delete
    suspend fun delete(card: SDCATCardRaw)
}

@Database(entities = [SDCATCardRaw::class], version = 1)
@TypeConverters(ByteListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun SDCATCardRawDao(): SDCATCardRawDao
}

class SDCATCardRepository @Inject constructor(private val sdcatCardRawDao: SDCATCardRawDao) {
    suspend fun getAllCards(): List<SDCATCardParsedData> {
        return sdcatCardRawDao.getAll()
            .map { SDCATCardRawData(it.type, it.rawMessage, it.rawCertificate).toParsed() }
    }

    suspend fun getCard(uuid: UUID): SDCATCardParsedData {
        return sdcatCardRawDao.getOne(uuid).let {
            SDCATCardRawData(
                it.type,
                it.rawMessage,
                it.rawCertificate
            ).toParsed()
        }
    }

    suspend fun addCard(sdcatCardRawData: SDCATCardRawData) {
        sdcatCardRawDao.insert(
            SDCATCardRaw(
                sdcatCardRawData.toUUID(),
                sdcatCardRawData.type,
                sdcatCardRawData.rawMessage,
                sdcatCardRawData.rawCertificate
            )
        )
    }

    suspend fun removeCard(sdcatCardRawData: SDCATCardRawData) {
        sdcatCardRawDao.delete(
            SDCATCardRaw(
                sdcatCardRawData.toUUID(),
                sdcatCardRawData.type,
                sdcatCardRawData.rawMessage,
                sdcatCardRawData.rawCertificate
            )
        )
    }
}
