package io.github.ziginsider.epam_laba_13

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.os.Binder
import android.os.Handler
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import io.github.ziginsider.epam_laba_13.utils.requestingLocationUpdates
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationResult
import android.os.HandlerThread
import android.app.NotificationChannel
import android.os.Build
import io.github.ziginsider.epam_laba_13.utils.loge

class LocationService : Service() {

    private var changingConfiguration = false
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var notificationManager: NotificationManager? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var serviceHandler: Handler? = null
    private var currentLocation: Location? = null
    private val binder = LocalBinder()

    override fun onCreate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                sendNewLocation(locationResult.lastLocation)
            }
        }

        createLocationRequest()
        addOnCompleteListener()

        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        serviceHandler = Handler(handlerThread.looper)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            notificationManager?.createNotificationChannel(NotificationChannel(CHANNEL_ID, name,
                    NotificationManager.IMPORTANCE_DEFAULT))
        }
    }

    override fun onBind(p0: Intent?): IBinder {
        stopForeground(true)
        changingConfiguration = false
        return binder
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

    private fun createLocationRequest() {
        locationRequest = LocationRequest().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun addOnCompleteListener() {
        try {
            fusedLocationClient?.lastLocation
                    ?.addOnCompleteListener { task ->
                        task.run {
                            if (isSuccessful && result != null) {
                                currentLocation = result
                            } else {
                                loge(TAG, "[ Failed to get location ]")
                            }
                        }
                    }
        } catch (e: SecurityException) {
            loge(TAG, "[ Lost location permission: $e ]")
            e.printStackTrace()
        }
    }

    companion object {

        private val TAG = LocationService::class.java.simpleName
        private const val PACKAGE_NAME = "io.github.ziginsider.epam_laba_13"
        private const val CHANNEL_ID = "channel_13"
        private const val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"
        private const val EXTRA_LOCATION = "$PACKAGE_NAME.location"
        private const val EXTRA_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.started_from_notification"
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
        private const val NOTIFICATION_ID = 12345678

    }
}
