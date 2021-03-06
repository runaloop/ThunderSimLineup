package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonVehicleStore
import com.catp.thundersimlineup.data.db.Changeset
import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.catp.thundersimlineup.whenNull
import toothpick.InjectConstructor

@InjectConstructor
class UpdateVehicleStore(
    val dao: LineupDao,
    val changeset: Changeset,
    private val checkAndUpdateTitle: CheckAndUpdateTitle,
    private val checkAndUpdateBR: CheckAndUpdateBR
) {

    fun process(vehicleStore: JsonVehicleStore) {
        val vehicles = dao.getVehicles()
        // Take different or a new
        val updatedVehicleList = vehicleStore.vehicleList.map { jsonVehicle ->
            vehicles.find { it.vehicleId == jsonVehicle.name }
                .whenNull {
                    //Its a new vehicle
                    return@map Vehicle.fromJson(jsonVehicle)
                    //just skip this item
                }
                ?.let {
                    if (
                        checkAndUpdateTitle.process(it, jsonVehicle) ||
                        checkAndUpdateBR.process(it, jsonVehicle)
                    ) {
                        return@map it
                    }
                }

            //its just the same, no need to update or insert for this item
            return@map null
        }.filterNotNull()
        // upsert different and new
        if (updatedVehicleList.isNotEmpty())
            dao.insertVehicles(updatedVehicleList)
    }
}