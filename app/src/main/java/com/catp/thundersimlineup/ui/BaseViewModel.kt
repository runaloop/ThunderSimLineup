package com.catp.thundersimlineup.ui

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel(){
    val inited: Boolean by lazy{
        onCreateAfterInject()
        true
    }
    abstract fun onCreateAfterInject()
}