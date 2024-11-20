package com.jakubmeysner.legitnik.util

import android.content.Context
import android.content.Intent
import android.net.Uri

fun showMap(geoLocation: Uri, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = geoLocation
    }
    context.startActivity(intent)
}
