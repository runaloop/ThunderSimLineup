package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonTeam
import com.catp.thundersimlineup.data.db.entity.TeamWithVehicleCrossRef
import com.catp.thundersimlineup.data.db.entity.VehicleStatus
import com.catp.thundersimlineup.whenNonNull
import com.catp.thundersimlineup.whenNull
import toothpick.InjectConstructor

@InjectConstructor
class FindNewVehicles {
    fun process(
        jsonTeam: JsonTeam,
        vehicleCrossRefList: List<TeamWithVehicleCrossRef>,
        teamId: Long
    ): List<TeamWithVehicleCrossRef> {
        return jsonTeam.vehicleIdList.map { vehicleId ->
            vehicleCrossRefList
                .find { it.vehicleId == vehicleId }
                .whenNull {
                    //its a new vehicle just add it
                    return@map TeamWithVehicleCrossRef(
                        teamId,
                        vehicleId,
                        VehicleStatus.NEW
                    )
                }.whenNonNull {
                    return@map null
                }
        }.filterNotNull()
    }
}