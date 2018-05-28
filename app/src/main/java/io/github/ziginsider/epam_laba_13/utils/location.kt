package io.github.ziginsider.epam_laba_13.utils

import android.content.Context
import android.location.Location
import android.preference.PreferenceManager
import io.github.ziginsider.epam_laba_13.R
import java.text.DateFormat
import java.util.*

const val KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates"

/**
 * Returns true if requesting location updates, otherwise returns false.
 *
 * @param context The [Context].
 */
fun requestingLocationUpdates(context: Context)
        = PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)

/**
 * Stores the location updates state in SharedPreferences.
 *
 * @param requestingLocationUpdates The location updates state.
 */
fun setRequestingLocationUpdates(context: Context, requestingLocationUpdates: Boolean) {
    PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
            .apply()
}

/**
 * Returns the [location] object as a human readable string.
 * @param location  The [Location].
 */
fun getLocationText(location: Location?) = if (location == null) {
    "Unknown location"
} else {
    "(${location.latitude}, ${location.longitude})"
}

/**
 * Gets the location title
 *
 * @param context The [Context].
 */
fun getLocationTitle(context: Context): String {
    return context.getString(R.string.location_updated,
            DateFormat.getDateTimeInstance().format(Date()))
}


