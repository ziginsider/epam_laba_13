package io.github.ziginsider.epam_laba_13.utils

import android.view.View

/**
 * Extension for View. Sets visibility [View.VISIBLE]
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Extension for View. Sets visibility [View.GONE]
 */
fun View.hide() {
    visibility = View.GONE
}