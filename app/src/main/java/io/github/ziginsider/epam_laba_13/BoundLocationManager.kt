package io.github.ziginsider.epam_laba_13

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.content.Context

class BoundLocationManager {

    class BoundLocationListener(val context: Context, val lifecycleOwner: LifecycleOwner)
        : LifecycleObserver {

        init {
            lifecycleOwner.lifecycle.addObserver(this)
        }

    }
    
    companion object {

        fun bindLocationListenerIn(context: Context, lifecycleOwner: LifecycleOwner) {

        }
    }
}