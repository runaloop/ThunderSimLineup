package com.catp.thundersimlineup.ui.vehiclelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.catp.thundersimlineup.ui.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import toothpick.InjectConstructor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@InjectConstructor
class VehicleListViewModel: BaseViewModel() {
    @Inject
    lateinit var lineupDao: LineupDao

    private val _vehicles = MutableLiveData<List<Vehicle>>()
    val vehicles: LiveData<List<Vehicle>> = _vehicles

    override fun onCreateAfterInject() {
        viewModelScope.launch {
            lineupDao.getVehiclesFlow().collectLatest { _vehicles.value = it }
        }
    }
}