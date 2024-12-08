package com.jakubmeysner.legitnik.data.sdcatcard.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardType
import java.util.UUID

@Entity(tableName = "SDCAT_card_raw_data", indices = [Index(value = ["hash"], unique = true)])
data class SDCATCardRawDataEntity(
    @PrimaryKey override val uuid: UUID,
    val hash: List<Byte>,
    override val type: SDCATCardType,
    @ColumnInfo("raw_message") override val rawMessage: List<Byte>,
    @ColumnInfo("raw_certificate") override val rawCertificate: List<Byte>,
) : SDCATCardRawDataEntityInterface
