package io.github.ziginsider.epam_laba_13

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import io.github.ziginsider.epam_laba_13.utils.KEY_REQUESTING_LOCATION_UPDATES
import io.github.ziginsider.epam_laba_13.utils.getLocationText
import io.github.ziginsider.epam_laba_13.utils.requestingLocationUpdates
import io.github.ziginsider.epam_laba_13.utils.toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var myReceiver: MyReceiver? = null
    private var service: LocationService? = null
    private var isBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myReceiver = MyReceiver()
        setContentView(R.layout.activity_main)

        if (requestingLocationUpdates(this)) {
            if (!checkPermission()) {
                requestPermission()
            }
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
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, str: String?) {
        if (str == KEY_REQUESTING_LOCATION_UPDATES) {
            setButtonState(sharedPreferences.getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false))
        }
    }

    private fun checkPermission() = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSION_LOCATION)
    }

    companion object {

        const val REQUEST_PERMISSION_LOCATION = 33
    }
}
