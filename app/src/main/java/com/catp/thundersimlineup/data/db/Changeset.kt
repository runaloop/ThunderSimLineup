package com.catp.thundersimlineup.data.db

import com.catp.thundersimlineup.data.db.entity.LineupEntity
import com.catp.thundersimlineup.data.db.entity.Vehicle
import toothpick.InjectConstructor

@InjectConstructor
class Changeset {
    fun reportVehicleBRChange(vehicle: Vehicle, oldBR: String) {

    }

    fun reportVehicleTitleChange(vehicle: Vehicle, oldTitle: String) {

    }

    fun reportVehicleAdded(vehicle: Vehicle, lineup: LineupEntity) {

    }

    fun reportLineupAdded(lineupName: String) {

    }
}