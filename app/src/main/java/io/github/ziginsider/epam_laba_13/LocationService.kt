package io.github.ziginsider.epam_laba_13

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.content.Context
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
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if (locationResult == null) {
                    loge(TAG, "[ Failed to get location ]")
                } else {
                    sendNewLocation(locationResult.lastLocation)
                }
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

    private fun sendNewLocation(location: Location) {
        logi(TAG, "[ New location = $location ]")
        currentLocation = location

        LocalBroadcastManager.getInstance(applicationContext)
                .sendBroadcast(Intent(ACTION_BROADCAST).apply {
                    putExtra(EXTRA_LOCATION, location)
                })

        if (serviceIsRunningInForeground(this)) {
            notificationManager?.notify(NOTIFICATION_ID, getNotification())
        }

    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The [Context].
     */
    fun serviceIsRunningInForeground(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (javaClass.name == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }

    private fun getNotification(): Notification? {
        val textLocation = getLocationText(currentLocation)
        val intent = Intent(this, LocationService::class.java).apply {
            putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)
        }
        val servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        val activityPendingIntent = PendingIntent.getActivity(this, 0,
                Intent(this, MainActivity::class.java), 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .addAction(R.drawable.ic_location_launch, getString(R.string.launch_activity),
                        activityPendingIntent)
                .addAction(R.drawable.ic_location_cancel,
                        getString(R.string.remove_location_updates), servicePendingIntent)
                .setContentText(textLocation)
                .setContentTitle(getLocationTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
        return builder.build()
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
