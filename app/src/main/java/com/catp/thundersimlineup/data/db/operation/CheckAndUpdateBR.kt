package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonVehicle
import com.catp.thundersimlineup.data.db.Changeset
import com.catp.thundersimlineup.data.db.entity.Vehicle
import toothpick.InjectConstructor

@InjectConstructor
class CheckAndUpdateBR(val changeset: Changeset) {
    fun process(vehicle: Vehicle, jsonVehicle: JsonVehicle): Boolean {
        if (vehicle.br != jsonVehicle.br) {
            val oldBR = vehicle.br
            vehicle.br = jsonVehicle.br
            return true
        }
        return false

    }
}