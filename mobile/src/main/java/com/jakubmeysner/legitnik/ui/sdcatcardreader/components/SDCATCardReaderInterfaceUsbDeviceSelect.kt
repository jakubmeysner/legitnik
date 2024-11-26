package com.jakubmeysner.legitnik.ui.sdcatcardreader.components

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.util.getFriendlyName

private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SDCATCardReaderInterfaceUsbDeviceSelect(
    enabled: Boolean,
    selectedUsbDevice: UsbDevice?,
    selectUsbDevice: (usbDevice: UsbDevice?) -> Unit,
) {
    val context = LocalContext.current
    val manager = remember(context) { context.getSystemService(UsbManager::class.java) }
    var deviceList by remember(context) { mutableStateOf(manager.deviceList) }

    var permissionRequestDevice by remember { mutableStateOf<UsbDevice?>(null) }

    val permissionIntent = remember(context) {
        PendingIntent.getBroadcast(
            context,
            0,
            Intent(ACTION_USB_PERMISSION),
            PendingIntent.FLAG_IMMUTABLE,
        )
    }

    DisposableEffect(context) {
        val usbReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                        deviceList = manager.deviceList
                    }

                    UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                        deviceList = manager.deviceList

                        if (selectedUsbDevice == null) {
                            selectUsbDevice(null)
                        }
                    }

                    ACTION_USB_PERMISSION -> {
                        val device = permissionRequestDevice

                        if (device != null) {
                            val permissionGranted = manager.hasPermission(device)

                            if (permissionGranted) {
                                selectUsbDevice(device)
                            }
                        }
                    }
                }
            }
        }

        context.registerReceiver(
            usbReceiver,
            IntentFilter(ACTION_USB_PERMISSION).apply {
                addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            },
            Context.RECEIVER_EXPORTED,
        )

        onDispose {
            context.unregisterReceiver(usbReceiver)
        }
    }

    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = it
        },
    ) {
        TextField(
            value = selectedUsbDevice?.getFriendlyName() ?: "",
            onValueChange = {},
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            readOnly = true,
            enabled = enabled,
            label = {
                Text(stringResource(R.string.sdcat_card_reader_interface_usb_card_reader))
            },
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.mdi_smart_card_reader),
                    contentDescription = null,
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )

        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = {
                expanded = false
            },
        ) {
            for ((deviceName, device) in deviceList) {
                key(deviceName) {
                    DropdownMenuItem(
                        text = {
                            Text(device.getFriendlyName())
                        },
                        onClick = {
                            permissionRequestDevice = device
                            manager.requestPermission(device, permissionIntent)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
