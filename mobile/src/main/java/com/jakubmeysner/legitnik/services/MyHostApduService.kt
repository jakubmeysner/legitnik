package com.jakubmeysner.legitnik.services

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardApdu
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardRepository
import com.jakubmeysner.legitnik.domain.apdu.ApduTransceiver
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class MyHostApduService(
    coroutineContext: CoroutineContext = SupervisorJob(),
) : HostApduService(), ClassSimpleNameLoggingTag {
    @Inject
    lateinit var sdcatCardRepository: SDCATCardRepository

    private val scope = CoroutineScope(coroutineContext)

    @Volatile
    private var certificateEfSelected = false

    @OptIn(ExperimentalStdlibApi::class)
    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray? {
        scope.launch {
            val commandApduList = commandApdu.asList()
            val commandApduString = commandApdu.joinToString(separator = ":") { it.toHexString() }
            Log.d(tag, "Received APDU: $commandApduString")

            try {
                val (cla, ins, p1) = commandApduList
                val card = sdcatCardRepository.getActiveOrDefaultCard()

                if (cla != ApduTransceiver.CLA) {
                    sendResponseApdu(ApduTransceiver.classNotSupportedSw.toByteArray())
                } else if (ins == ApduTransceiver.INS_SELECT_FILE) {
                    if (card == null) {
                        sendResponseApdu(ApduTransceiver.fileNotFoundSw.toByteArray())
                        return@launch
                    }

                    if (p1 == ApduTransceiver.P1_SELECT_FILE_BY_DF_NAME) {
                        val cardDfName = SDCATCardApdu.typeToDfName[card.type]
                        val dataLength = commandApduList[4]
                        val data = commandApduList.subList(5, 5 + dataLength)

                        if (cardDfName != data) {
                            sendResponseApdu(ApduTransceiver.fileNotFoundSw.toByteArray())
                        } else {
                            sendResponseApdu(ApduTransceiver.okSw.toByteArray())
                        }
                    } else if (p1 == ApduTransceiver.P1_SELECT_FILE_BY_EF_IDENTIFIER) {
                        val dataLength = commandApduList[4]
                        val data = commandApduList.subList(5, 5 + dataLength)

                        when (data) {
                            SDCATCardApdu.messageEfIdentifier -> {
                                certificateEfSelected = false
                                sendResponseApdu(ApduTransceiver.okSw.toByteArray())
                            }

                            SDCATCardApdu.certificateEfIdentifier -> {
                                certificateEfSelected = true
                                sendResponseApdu(ApduTransceiver.okSw.toByteArray())
                            }

                            else -> {
                                sendResponseApdu(ApduTransceiver.fileNotFoundSw.toByteArray())
                            }
                        }
                    } else {
                        sendResponseApdu(ApduTransceiver.functionNotSupportedSw.toByteArray())
                    }
                } else if (ins == ApduTransceiver.INS_READ_BINARY) {
                    if (card == null) {
                        sendResponseApdu(
                            ApduTransceiver.commandNotAllowedNoCurrentEfSw.toByteArray()
                        )

                        return@launch
                    }

                    val offset = (
                        (commandApduList[2].toInt() and 0xff) shl 8
                            or (commandApduList[3].toInt() and 0xff)
                        )

                    val expectedResponseSize = commandApduList[3].toInt() and 0xff
                    val responseSize = if (expectedResponseSize == 0) 256 else expectedResponseSize
                    val file = if (certificateEfSelected) card.rawCertificate else card.rawMessage

                    if (offset >= file.size) {
                        sendResponseApdu(ApduTransceiver.fileNotFoundSw.toByteArray())
                    } else {
                        val fileChunk = file.subList(
                            offset, (offset + responseSize).coerceAtMost(file.size)
                        )

                        sendResponseApdu(fileChunk.toByteArray() + ApduTransceiver.okSw)
                    }
                } else {
                    sendResponseApdu(ApduTransceiver.functionNotSupportedSw.toByteArray())
                }
            } catch (e: Exception) {
                Log.e(tag, "An exception occurred while processing command $commandApduString", e)
            }
        }

        return null
    }

    override fun onDeactivated(reason: Int) {
        Log.d(tag, "Service was deactivated because of reason $reason")
        scope.coroutineContext.job.cancelChildren()
    }

    override fun onDestroy() {
        scope.cancel()
    }
}
