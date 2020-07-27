package com.catp.thundersimlineup.ui.vehiclelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.catp.thundersimlineup.data.db.entity.Vehicle
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
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


    val vehicles: LiveData<List<Vehicle>> = _vehicles
    private val cs = CompositeDisposable()

    override fun onCleared() {
        cs.clear()
        super.onCleared()
    }

    fun viewCreated() {
        vehicleRequestInteractor.getVehicles()
            .subscribe {
                _vehicles.postValue(it)
            }.addTo(cs)
    }


}