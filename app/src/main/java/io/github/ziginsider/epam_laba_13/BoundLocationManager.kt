package io.github.ziginsider.epam_laba_13

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context

class BoundLocationManager {

    class BoundLocationListener(val context: Context, val lifecycleOwner: LifecycleOwner)
        : LifecycleObserver {

        init {
            lifecycleOwner.lifecycle.addObserver(this)
        }

        //@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun initLocationActivityState() {

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


    }

    companion object {

        fun bindLocationListenerIn(context: Context, lifecycleOwner: LifecycleOwner) {

        }
    }
}