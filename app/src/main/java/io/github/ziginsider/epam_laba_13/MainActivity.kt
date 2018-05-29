package io.github.ziginsider.epam_laba_13

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import io.github.ziginsider.epam_laba_13.utils.KEY_REQUESTING_LOCATION_UPDATES
import io.github.ziginsider.epam_laba_13.utils.getLocationText
import io.github.ziginsider.epam_laba_13.utils.requestingLocationUpdates
import io.github.ziginsider.epam_laba_13.utils.toast
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener
        , OnMapReadyCallback {

    private lateinit var myReceiver: MyReceiver
    private var service: LocationService? = null
    private var isBound = false
    private var map: GoogleMap? = null
    private var isLastLocation = false
    private var lastLatitude = 0.0
    private var lastLongitude = 0.0
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myReceiver = MyReceiver()
        setContentView(R.layout.activity_main)

        if (requestingLocationUpdates(this)) {
            if (!checkPermission()) {
                requestPermission()
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this)
        requestLocationButton.setOnClickListener {
            if (checkPermission()) {
                service?.requestLocationUpdates()
            } else {
                requestPermission()
            }
        }
        removeLocationButton.setOnClickListener {
            service?.removeLocationUpdates()
        }
        setButtonState(requestingLocationUpdates(this))
        bindService(Intent(this, LocationService::class.java), serviceConnection,
                Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                IntentFilter(LocationService.ACTION_BROADCAST))
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver)
        super.onPause()
    }

    override fun onStop() {
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_LOCATION -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toast("Permission granted")
                } else {
                    toast("Permission denied")
                }
            }
        }
    }

    private inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val location = intent?.getParcelableExtra<Location>(LocationService.EXTRA_LOCATION)
            location?.let {
                textLocation.text = getLocationText(it)

                if (isLastLocation) {
                    marker?.remove()
                    val line = map?.addPolyline(PolylineOptions()
                            .add(LatLng(lastLatitude, lastLongitude),
                                    LatLng(it.latitude, it.longitude))
                            .width(LINE_WIDTH)
                            .color(Color.RED))

                    lastLatitude = it.latitude
                    lastLongitude = it.longitude
                    marker = map?.addMarker(MarkerOptions()
                            .position(LatLng(lastLongitude, lastLongitude))
                            .title("Current position"))
                } else {
                    isLastLocation = true
                    lastLatitude = it.latitude
                    lastLongitude = it.longitude
                    map?.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(LatLng(it.latitude, it.longitude), MAP_ZOOM))
                }
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, str: String?) {
        if (str == KEY_REQUESTING_LOCATION_UPDATES) {
            setButtonState(sharedPreferences.getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false))
        }
    }

    private fun setButtonState(requestingLocationUpdates: Boolean) {
        if (requestingLocationUpdates) {
            requestLocationButton.isEnabled = false
            removeLocationButton.isEnabled = true
        } else {
            requestLocationButton.isEnabled = true
            removeLocationButton.isEnabled = false
        }
    }

    private fun checkPermission() = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSION_LOCATION)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        map = googleMap


    }

    companion object {

        const val REQUEST_PERMISSION_LOCATION = 33
        private const val LINE_WIDTH = 12.0f
        private const val MAP_ZOOM = 15.0f
    }
}
