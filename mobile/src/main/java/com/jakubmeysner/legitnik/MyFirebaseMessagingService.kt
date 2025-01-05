package com.jakubmeysner.legitnik

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.jakubmeysner.legitnik.ui.settings.SettingsViewModel
import javax.inject.Inject

class MyFirebaseMessagingService : FirebaseMessagingService(), ClassSimpleNameLoggingTag {

    @Inject
    lateinit var settingsViewModel: SettingsViewModel

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(tag, "Refreshed token: $token")

        CoroutineScope(Dispatchers.IO).launch {
            settingsViewModel.subscribeToFcmTopicsOnTokenRefresh()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(tag, "Message received: ${message.messageId}")
    }
}
