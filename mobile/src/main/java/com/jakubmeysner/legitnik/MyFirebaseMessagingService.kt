package com.jakubmeysner.legitnik

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.jakubmeysner.legitnik.data.settings.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class MyFirebaseMessagingService(
    coroutineContext: CoroutineContext = SupervisorJob(),
) : FirebaseMessagingService(), ClassSimpleNameLoggingTag {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val scope = CoroutineScope(coroutineContext)

    override fun onNewToken(token: String) {
        Log.d(tag, "Refreshed token: $token")

        scope.launch {
            settingsRepository.subscribeToFcmTopicsOnTokenRefresh()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(tag, "Message received: ${message.messageId}")
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
