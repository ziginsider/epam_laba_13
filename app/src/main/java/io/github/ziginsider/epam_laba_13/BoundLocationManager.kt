package io.github.ziginsider.epam_laba_13

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.*
import android.os.IBinder
import android.preference.PreferenceManager
import android.widget.Button
import io.github.ziginsider.epam_laba_13.utils.*

/**
 * implements method [bindLocationRunnerIn] for create [LocationRunner]
 *
 * @since 2018-05-29
 * @author Alex Kisel
 */
class BoundLocationManager {

    /**
     * Runs a location requests with help a bound and started service [LocationService]
     */
    class LocationRunner(lifecycleOwner: LifecycleOwner,
                         private val context: Context,
                         private val requestLocationButton: Button,
                         private val removeLocationButton: Button)
        : LifecycleObserver, SharedPreferences.OnSharedPreferenceChangeListener {

        init {
            lifecycleOwner.lifecycle.addObserver(this)
        }

        private var service: LocationService? = null
        private var isBound = false

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun registerSharedPreferenceListener() {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .registerOnSharedPreferenceChangeListener(this)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun initLocationActivityUiState() {
            logi(context.javaClass.simpleName, "[ Activity onStart() ]")
            requestLocationButton.setOnClickListener {
                service?.requestLocationUpdates()
            }
            removeLocationButton.setOnClickListener {
                service?.removeLocationUpdates()
            }
            setButtonState(requestingLocationUpdates(context))
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun bindLocationService() {
            context.bindService(Intent(context, LocationService::class.java), serviceConnection,
                    Context.BIND_AUTO_CREATE)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun unbindLocationService() {
            logi(context.javaClass.simpleName, "[ Activity onStop() ]")
            if (isBound) {
                context.unbindService(serviceConnection)
                isBound = false
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun removeSharedPreferenceListener() {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences,
                                               str: String?) {
            if (str == KEY_REQUESTING_LOCATION_UPDATES) {
                setButtonState(sharedPreferences.getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false))
            }
        }

        private fun setButtonState(requestingLocationUpdates: Boolean) {
            if (requestingLocationUpdates) {
                requestLocationButton.hide()
                removeLocationButton.show()
            } else {
                requestLocationButton.show()
                removeLocationButton.hide()
            }
        }

        private val serviceConnection = object : ServiceConnection {

            override fun onServiceConnected(className: ComponentName?, localService: IBinder?) {
                val binder = localService as LocationService.LocalBinder
                service = binder.service
                isBound = true
            }

            override fun onServiceDisconnected(className: ComponentName?) {
                service = null
                isBound = false
            }
        }
    }

    companion object {

        fun bindLocationRunnerIn(lifecycleOwner: LifecycleOwner, context: Context,
                                 requestButton: Button, removeButton: Button) {
            LocationRunner(lifecycleOwner, context, requestButton, removeButton)
        }
    }
}
