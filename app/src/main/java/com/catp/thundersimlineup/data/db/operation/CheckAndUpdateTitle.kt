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
        if (jsonVehicle.locale!!.fullEnglishTitle != vehicle.title) {
            val oldTitle = vehicle.title
            vehicle.title = jsonVehicle.locale!!.fullEnglishTitle
            changeset.reportVehicleTitleChange(vehicle, oldTitle)
            return true
        }
        return false
    }
}