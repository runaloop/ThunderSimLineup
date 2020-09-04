package com.catp.thundersimlineup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.catp.thundersimlineup.notifications.DailyNotificator
import com.catp.thundersimlineup.ui.lineuplist.PushFavoriteVehicleInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var dailyNotificator: DailyNotificator
    @Inject
    lateinit var pushFavoriteVehicleInteractor: PushFavoriteVehicleInteractor


    private val selectedItems = mutableSetOf<Vehicle>()

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

    fun onFavoriteChange(vehicle: Vehicle) {
        vehicle.isFavorite = !vehicle.isFavorite
        selectedItems += vehicle
    }

    fun pushFavorites() {
        if (selectedItems.isNotEmpty()) {
            val items = selectedItems.toList()
            selectedItems.clear()
            viewModelScope.launch(Dispatchers.IO) {
                pushFavoriteVehicleInteractor.push(items)
            }
        }
    }
}