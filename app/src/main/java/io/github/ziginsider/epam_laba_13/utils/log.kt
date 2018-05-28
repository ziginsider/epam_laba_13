package io.github.ziginsider.epam_laba_13.utils

import android.util.Log
import io.github.ziginsider.epam_laba_13.BuildConfig

/**
 * Extension fun for logging info
 */
fun logi(className: String, message: String) {
    if (BuildConfig.DEBUG) Log.i(className, message)
}

/**
 * Extension fun for logging errors
 */
fun loge(className: String, message: String) {
    Log.e(className, message)
}