package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonTeam
import com.catp.thundersimlineup.data.db.entity.TeamWithVehicleCrossRef
import com.catp.thundersimlineup.data.db.entity.VehicleStatus
import toothpick.InjectConstructor

/**
 * Marks vehicles with deleted status, if it not present in jsonTeam
 */
@InjectConstructor
class FindVehiclesToDelete {
    fun process(
        jsonTeam: JsonTeam,
        vehicleCrossRefList: List<TeamWithVehicleCrossRef>
    ): List<TeamWithVehicleCrossRef> {
        return vehicleCrossRefList.map { vehicle ->
            if (!jsonTeam.vehicleIdList.contains(vehicle.vehicleId)) {
                vehicle.status = VehicleStatus.DELETED
                vehicle
            } else
                null
        }.filterNotNull()
    }
}