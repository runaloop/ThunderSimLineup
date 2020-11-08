package com.catp.thundersimlineup.ui

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel(){

    /**
     * This method and field is used, because by default toothpick doesnt wont do clean solution
     * for ViewModel constructor injection, so I have to call this method every time, after injection, so the
     * ViewModel have ability to do after inject initialization
     */
    private val inited: Boolean by lazy{
        onCreateAfterInject()
        true
    }
    fun afterInject(){
        inited
    }
    abstract fun onCreateAfterInject()
}