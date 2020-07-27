package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonTeam
import com.catp.thundersimlineup.data.db.Changeset
import com.catp.thundersimlineup.data.db.entity.TeamWithVehicleCrossRef
import com.catp.thundersimlineup.data.db.entity.VehicleStatus
import toothpick.InjectConstructor
import javax.inject.Inject

/**
 * Marks vehicles with deleted status, if it not present in jsonTeam
 */
@InjectConstructor
class FindVehiclesToDelete {
    @Inject
    lateinit var changeset: Changeset
    fun process(
        jsonTeam: JsonTeam,
        vehicleCrossRefList: List<TeamWithVehicleCrossRef>,
        lineupName: String=""
    ): List<TeamWithVehicleCrossRef> {
        return vehicleCrossRefList.mapNotNull { vehicle ->
            if (!jsonTeam.vehicleIdList.contains(vehicle.vehicleId)) {
                changeset.reportVehicleRemoved(vehicle.vehicleId, lineupName)
                vehicle.status = VehicleStatus.DELETED
                vehicle
            } else
                null
        }
    }
}