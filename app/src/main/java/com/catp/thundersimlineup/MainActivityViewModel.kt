package com.catp.thundersimlineup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.catp.thundersimlineup.notifications.DailyNotificator
import javax.inject.Inject

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var dailyNotificator: DailyNotificator

    val notificationTaskCreateDelegate: String by lazy {
        dailyNotificator.createNotificationTask(application)
        "OK"
    }
    init {
        log("🍏 inited")
    }
    override fun onCleared() {
        log("🍏 onCleared")
        super.onCleared()
    }
}