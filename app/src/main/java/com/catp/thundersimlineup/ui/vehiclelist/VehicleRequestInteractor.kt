package com.catp.thundersimlineup.ui.vehiclelist

import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class VehicleRequestInteractor {
    @Inject
    lateinit var lineupDao: LineupDao

    suspend fun getVehicles(): List<Vehicle> =
        withContext(Dispatchers.IO) {
            lineupDao.getVehicles()
        }
}