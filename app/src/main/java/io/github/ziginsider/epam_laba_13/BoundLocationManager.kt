package io.github.ziginsider.epam_laba_13

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.content.SharedPreferences
import android.content.SyncRequest
import android.preference.PreferenceManager
import android.widget.Button
import io.github.ziginsider.epam_laba_13.utils.KEY_REQUESTING_LOCATION_UPDATES

class BoundLocationManager {

    class BoundLocationListener(val context: Context,
                                val lifecycleOwner: LifecycleOwner,
                                val requestLocationButton: Button,
                                val removeLocationButton: Button)
        : LifecycleObserver, SharedPreferences.OnSharedPreferenceChangeListener {

        init {
            lifecycleOwner.lifecycle.addObserver(this)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun initLocationActivityState() {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .registerOnSharedPreferenceChangeListener(this)
            requestLocationButton.setOnClickListener {
                lifecycleOwner.
            }

        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun registerReceiver() {

        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun unregisterReceiver() {

        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun unbindService() {

        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences,
                                               str: String?) {
            if (str == KEY_REQUESTING_LOCATION_UPDATES) {
                setButtonState(sharedPreferences.getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false))
            }
        }
    }

    companion object {

        fun bindLocationListenerIn(context: Context, lifecycleOwner: LifecycleOwner, requestButton: Button, removeButton: Button) =
            BoundLocationListener(context, lifecycleOwner, requestButton, removeButton)
    }
}