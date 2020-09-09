package com.catp.thundersimlineup.ui.vehiclelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catp.thundersimlineup.data.db.entity.Vehicle
import kotlinx.coroutines.launch
import toothpick.InjectConstructor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@InjectConstructor
class VehicleListViewModel : ViewModel() {
    @Inject
    lateinit var vehicleRequestInteractor: VehicleRequestInteractor


    private val _vehicles = MutableLiveData<List<Vehicle>>()

    val vehicles: LiveData<List<Vehicle>> = _vehicles

    override fun onCleared() {
        super.onCleared()
    }

    fun viewCreated() {
        viewModelScope.launch {
            val vehicles = vehicleRequestInteractor.getVehicles()
            _vehicles.value = vehicles
        }
    }


}