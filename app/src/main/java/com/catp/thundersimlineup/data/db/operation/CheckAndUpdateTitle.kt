package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonVehicle
import com.catp.thundersimlineup.data.db.Changeset
import com.catp.thundersimlineup.data.db.entity.Vehicle
import toothpick.InjectConstructor

@InjectConstructor
class CheckAndUpdateTitle(val changeset: Changeset) {
    fun process(
        vehicle: Vehicle,
        jsonVehicle: JsonVehicle
    ): Boolean {
        if (jsonVehicle.locale!!.title != vehicle.title) {
            vehicle.title = jsonVehicle.locale!!.title
            return true
        }
        return false
    }
}