package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonTeam
import com.catp.thundersimlineup.data.db.Changeset
import com.catp.thundersimlineup.data.db.entity.TeamWithVehicleCrossRef
import com.catp.thundersimlineup.data.db.entity.VehicleStatus
import com.catp.thundersimlineup.whenNonNull
import com.catp.thundersimlineup.whenNull
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class FindNewVehicles {

    @Inject
    lateinit var changeset: Changeset

    fun process(
        jsonTeam: JsonTeam,
        vehicleCrossRefList: List<TeamWithVehicleCrossRef>,
        teamId: Long,
        lineupName: String = ""
    ): List<TeamWithVehicleCrossRef> {
        return jsonTeam.vehicleIdList.map { vehicleId ->
            vehicleCrossRefList
                .find { it.vehicleId == vehicleId }
                .whenNull {
                    //its a new vehicle just add it
                    changeset.reportVehicleAdded(vehicleId, lineupName)
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