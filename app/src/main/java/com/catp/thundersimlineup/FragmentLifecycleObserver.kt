package com.catp.thundersimlineup

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class LoggingLifecycleObserver(private val lifecycle: Lifecycle, val title: String) :
    LifecycleObserver {
    private fun onEvent(event: String) {
        println("$title $event")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        onEvent("onCreate")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(){
        onEvent("onStart")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        onEvent("onResume")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(){
        onEvent("onPause")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(){
        onEvent("onStop")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){
        onEvent("onDestroy")
    }

    companion object {
        fun registerLogging(lifecycle: Lifecycle, title: String) {
            lifecycle.addObserver(LoggingLifecycleObserver(lifecycle, title))
        }
    }
}