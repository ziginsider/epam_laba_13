package io.github.ziginsider.epam_laba_13

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import com.google.android.gms.maps.GoogleMap

class BoundMapManager {

    class BoundMapListener(lifecycleOwner: LifecycleOwner,
                           val map: GoogleMap)
        : LifecycleObserver {

        init {
            lifecycleOwner.lifecycle.addObserver(this)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun registerReceiver() {

        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun unregisterReceiver() {

        }
    }

    companion object {

        fun bindMapListenerIn(lifecycleOwner: LifecycleOwner, map: GoogleMap) {
            BoundMapListener(lifecycleOwner, map)
        }
    }
}