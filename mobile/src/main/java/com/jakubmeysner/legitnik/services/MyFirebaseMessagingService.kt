package com.jakubmeysner.legitnik.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService(), ClassSimpleNameLoggingTag {
    override fun onNewToken(token: String) {
        Log.d(tag, "Refreshed token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(tag, "Message received: ${message.messageId}")
    }
}
