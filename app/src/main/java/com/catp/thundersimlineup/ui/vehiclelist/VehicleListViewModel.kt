package com.catp.thundersimlineup.ui.vehiclelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.catp.thundersimlineup.data.db.entity.Vehicle
import toothpick.InjectConstructor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@InjectConstructor
class VehicleListViewModel : ViewModel() {


    @Inject
    lateinit var vehicleRequestInteractor: VehicleRequestInteractor


    private val _vehicles = MutableLiveData<List<Vehicle>>().apply {
        //
    }

    private val _text = MutableLiveData<String>().apply {

    }

    val vehicles: LiveData<List<Vehicle>> = _vehicles
    val text: LiveData<String> = _text

    fun viewCreated() {
        vehicleRequestInteractor.getVehicles()
            .subscribe {
                _vehicles.postValue(it)
            }
    }


}