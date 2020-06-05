package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonVehicleStore
import com.catp.thundersimlineup.data.db.Changeset
import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.catp.thundersimlineup.whenNonNull
import com.catp.thundersimlineup.whenNull
import toothpick.InjectConstructor

@InjectConstructor
class UpdateVehicleStore(
    val dao: LineupDao,
    val changeset: Changeset,
    val checkAndUpdateTitle: CheckAndUpdateTitle,
    val checkAndUpdateBR: CheckAndUpdateBR
) {

    fun process(vehicleStore: JsonVehicleStore) {
        val vehicles = dao.getVehicles()
        // Take different or a new
        val updatedVehicleList = vehicleStore.vehicleList.map { jsonVehicle ->
            vehicles.find { it.vehicleId == jsonVehicle.name }
                .whenNonNull {
                    if (
                        checkAndUpdateTitle.process(this, jsonVehicle) ||
                        checkAndUpdateBR.process(this, jsonVehicle)
                    ) {
                        return@map this
                    }
                }
                .whenNull {
                    //Its a new vehicle
                    return@map Vehicle.fromJson(jsonVehicle)
                    //just skip this item
                }
            //its just the same, no need to update or insert for this item
            return@map null
        }.filterNotNull()
        // upsert different and new
        if (updatedVehicleList.isNotEmpty())
            dao.upsertVehicles(updatedVehicleList)
    }
}