package io.github.ziginsider.epam_laba_13

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.Location
import android.support.v4.content.LocalBroadcastManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import io.github.ziginsider.epam_laba_13.utils.logi

/**
 * implements method [bindMapListenerIn] for create [BoundMapListener]
 *
 * @since 2018-05-29
 * @author Alex Kisel
 */
class BoundMapManager {

    /**
     * Implements listener with help [MyReceiver] for location updates and draws the changes
     * on [GoogleMap]
     */
    class BoundMapListener(lifecycleOwner: LifecycleOwner,
                           private val context: Context,
                           private val map: GoogleMap)
        : LifecycleObserver {

        private var myReceiver = MyReceiver()
        private var isLastLocation = false
        private var lastLatitude = 0.0
        private var lastLongitude = 0.0
        private var marker: Marker? = null

        init {
            lifecycleOwner.lifecycle.addObserver(this)
            LocalBroadcastManager.getInstance(context).registerReceiver(myReceiver,
                    IntentFilter(LocationService.ACTION_BROADCAST))
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun registerReceiver() {
            logi(context.javaClass.simpleName, "[ Activity onResume() ]")
            LocalBroadcastManager.getInstance(context).registerReceiver(myReceiver,
                    IntentFilter(LocationService.ACTION_BROADCAST))
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun unregisterReceiver() {
            logi(context.javaClass.simpleName, "[ Activity onPause() ]")
            LocalBroadcastManager.getInstance(context).unregisterReceiver(myReceiver)
        }

        private inner class MyReceiver : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val location = intent?.getParcelableExtra<Location>(LocationService.EXTRA_LOCATION)
                location?.let {
                    if (isLastLocation) {
                        addNewLine(it)
                        lastLatitude = it.latitude
                        lastLongitude = it.longitude
                        marker?.position = LatLng(lastLatitude, lastLongitude)
                    } else {
                        isLastLocation = true
                        lastLatitude = it.latitude
                        lastLongitude = it.longitude
                        initMap(it)
                    }
                }
            }

            private fun addNewLine(location: Location) {
                map.addPolyline(PolylineOptions()
                        .add(LatLng(lastLatitude, lastLongitude),
                                LatLng(location.latitude, location.longitude))
                        .width(LINE_WIDTH)
                        .color(Color.RED)
                        .clickable(true))
            }

            private fun initMap(location: Location) {
                map.run {
                    marker = addMarker(MarkerOptions()
                            .position(LatLng(lastLatitude, lastLongitude))
                            .title("Current position"))
                    animateCamera(CameraUpdateFactory
                            .newLatLngZoom(LatLng(location.latitude, location.longitude), MAP_ZOOM))
                    setOnPolylineClickListener { polyline ->
                        polyline.color = polyline.color xor 0x00ffffff
                    }
                }
            }
        }

        companion object {

            private const val LINE_WIDTH = 12.0f
            private const val MAP_ZOOM = 15.0f
        }

    }

    companion object {

        fun bindMapListenerIn(lifecycleOwner: LifecycleOwner, context: Context, map: GoogleMap) {
            BoundMapListener(lifecycleOwner, context, map)
        }
    }
}