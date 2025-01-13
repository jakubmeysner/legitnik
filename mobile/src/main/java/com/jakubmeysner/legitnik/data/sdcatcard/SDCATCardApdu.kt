package com.jakubmeysner.legitnik.data.sdcatcard

import com.jakubmeysner.legitnik.util.b

object SDCATCardApdu {
    private val rid: List<Byte> = listOf(0xD6.b, 0x16, 0x00, 0x00, 0x30)

    val typeToDfName = mapOf(
        SDCATCardType.STUDENT to rid + 0x01 + 0x01,
        SDCATCardType.DOCTORAL_CANDIDATE to rid + 0x01 + 0x02,
        SDCATCardType.ACADEMIC_TEACHER to rid + 0x01 + 0x03,
    )

    val messageEfIdentifier: List<Byte> = listOf(0x00, 0x02)
    val certificateEfIdentifier: List<Byte> = listOf(0x00, 0x01)
}
