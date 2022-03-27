package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonVehicle
import com.catp.thundersimlineup.data.db.Changeset
import com.catp.thundersimlineup.data.db.entity.Vehicle
import toothpick.InjectConstructor

@InjectConstructor
class CheckAndUpdateBR(val changeset: Changeset) {
    fun process(vehicle: Vehicle, jsonVehicle: JsonVehicle): Boolean {
        if (vehicle.br != jsonVehicle.BR) {
            vehicle.br = jsonVehicle.BR
            return true
        }
        return false

    }
}