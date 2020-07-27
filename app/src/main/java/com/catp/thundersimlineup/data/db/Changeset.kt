package com.catp.thundersimlineup.data.db

import android.app.Application
import com.catp.thundersimlineup.LocalDateProvider
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.data.db.entity.Change
import com.catp.thundersimlineup.data.db.entity.Vehicle
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class Changeset {

    @Inject
    lateinit var changeDao: ChangeDao

    @Inject
    lateinit var lineupDao: LineupDao

    @Inject
    lateinit var context: Application

    @Inject
    lateinit var dateProvider: LocalDateProvider

    //vehicle_id and oldBr
    //private val vehicleBRChanged = mutableListOf<Pair<Vehicle, String>>()
    private val reportVehicleAdded = mutableListOf<Pair<String, String>>()

    //vehicle_id and lineup_name
    private val reportVehicleDeleted = mutableListOf<Pair<String, String>>()

    private fun writeLineups(
        data: List<Pair<String, String>>,
        allVehicles: List<Vehicle>,
        strTemplate: Int
    ) {
        val result = data.groupBy({ it.second }) { (vehicleId, lineup) ->
            allVehicles.find { it.vehicleId == vehicleId }!!
        }
            .mapNotNull { (lineup, vehicles) ->
                vehicles.map { vehicle ->
                    context.getString(strTemplate, vehicle.title, lineup)
                }.map {
                    Change(text = it, date = dateProvider.now())
                }
            }.flatten()
        changeDao.insertChanges(result)
    }

    fun writeChanges() {
        val vehicleIds = listOf(reportVehicleAdded, reportVehicleDeleted).flatten().map { it.first }
        if (vehicleIds.isNotEmpty()) {
            val vehicles = lineupDao.getVehicles().filter { it.vehicleId in vehicleIds }
            writeLineups(reportVehicleAdded, vehicles, R.string.change_log_add)
            writeLineups(reportVehicleDeleted, vehicles, R.string.change_log_del)
        }
    }

    fun reportVehicleBRChange(vehicle: Vehicle, oldBR: String) {
        //vehicleBRChanged += Pair(vehicle, oldBR)
    }

    fun reportVehicleAdded(vehicleId: String, lineupName: String) {
        reportVehicleAdded += Pair(vehicleId, lineupName)
    }

    fun reportVehicleRemoved(vehicleId: String, lineupName: String) {
        reportVehicleDeleted += Pair(vehicleId, lineupName)
    }

}