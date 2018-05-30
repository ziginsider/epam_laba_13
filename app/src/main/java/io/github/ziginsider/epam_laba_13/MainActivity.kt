package io.github.ziginsider.epam_laba_13

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*
import io.github.ziginsider.epam_laba_13.utils.*

/**
 * Activity that uses a bound and started service [LocationService] for location updates.
 *
 * After requesting location updates, when the activity ceases to be in the foreground,
 * the service promotes itself to a foreground service and continues receiving location updates.
 * When the activity comes back to the foreground, the foreground service stops, and the
 * notification associated with that foreground service is removed.
 *
 * Activity draws a location path (polylines) on a map
 *
 * @since 2018-05-28
 * @author Alex Kisel
 */
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (requestingLocationUpdates(this)) {
            if (!checkPermission()) {
                requestPermission()
            } else {
                bindLocationListener()
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun bindLocationListener() {
        BoundLocationManager.bindLocationListenerIn(this, this,
                requestLocationButton, removeLocationButton)
    }

    private fun bindMapListener(map: GoogleMap) {
        BoundMapManager.bindMapListenerIn(this, this, map)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_LOCATION -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toast("Permission granted")
                    bindLocationListener()
                } else {
                    toast("Permission denied. Unfortunately, the app won't work correctly")
                    requestLocationButton.hide()
                    removeLocationButton.hide()
                }
            }
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
        bindMapListener(googleMap)
    }

    companion object {

        const val REQUEST_PERMISSION_LOCATION = 33
    }
}
