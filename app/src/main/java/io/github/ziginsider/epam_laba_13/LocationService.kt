package io.github.ziginsider.epam_laba_13

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.NotificationManager
import android.location.Location
import android.os.Binder
import android.os.Handler
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationResult
import android.os.HandlerThread
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import io.github.ziginsider.epam_laba_13.utils.*

class LocationService : Service() {
    private var changingConfiguration = false
    private val notificationManager: NotificationManager? = null
    private val locationRequest: LocationRequest? = null
    private val locationCallback: LocationCallback? = null
    private val serviceHandler: Handler? = null
    private val currentLocation: Location? = null
    private val binder = LocalBinder()

    override fun onBind(p0: Intent?): IBinder {
        stopForeground(true)
        changingConfiguration = false
        return binder
    }

    override fun onRebind(intent: Intent) {
        logi(TAG, "[ in onRebind() ]")
        stopForeground(true)
        changingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (!changingConfiguration && requestingLocationUpdates(this)) {
            startForeground(NOTIFICATION_ID, getNotification())
        }
        return true
    }

    inner class LocalBinder : Binder() {
        internal var service: LocationService? = null
            get() = this@LocationService
            private set
    }

    companion object {



        private val TAG = LocationService::class.java.simpleName
        private const val PACKAGE_NAME = "io.github.ziginsider.epam_laba_13"
        private const val CHANNEL_ID = "channel_1"
        private const val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"
        private const val EXTRA_LOCATION = "$PACKAGE_NAME.location"
        private const val EXTRA_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.started_from_notification"
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
                = UPDATE_INTERVAL_IN_MILLISECONDS / 2
        private const val NOTIFICATION_ID = 12345678

    }
}
