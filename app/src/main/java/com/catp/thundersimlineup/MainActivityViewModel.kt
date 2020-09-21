package com.catp.thundersimlineup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.catp.thundersimlineup.notifications.DailyNotificator
import com.catp.thundersimlineup.ui.lineuplist.PushFavoriteVehicleInteractor
import com.prolificinteractive.materialcalendarview.CalendarDay
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


    private val _calendarDate = MutableLiveData<CalendarDay>().apply {
        value = CalendarDay.today()
    }
    val calendarDate = _calendarDate as LiveData<CalendarDay>

    override fun onCleared() {
        log("🍏 onCleared")
        super.onCleared()
    }

    fun onDateChanged(date: CalendarDay) {
        _calendarDate.value = date
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