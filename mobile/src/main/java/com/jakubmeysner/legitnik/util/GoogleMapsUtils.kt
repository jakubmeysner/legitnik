package com.jakubmeysner.legitnik.util

import android.content.Intent
import android.net.Uri

fun navigateInMaps(latitude: Double, longitude: Double): Intent {
    val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    return mapIntent
}
