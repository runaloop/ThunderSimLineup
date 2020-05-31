package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonLineup
import com.catp.thundersimlineup.data.db.LineupDao

class UpdateVehicleCrossRef(
    private val dao: LineupDao,
    private val findVehiclesToDelete: FindVehiclesToDelete,
    private val findNewVehicles: FindNewVehicles
) {
    fun process(jsonLineups: List<JsonLineup>) {
        //query current xrefs
        var currentXrefs = dao.getTeamWithVehicleCrossRef()
        val currentLineups = dao.getLineupsEntity()

        //find xrefs needs to be deleted
        jsonLineups.forEach { jsonLineup ->
            val lineupEntity = currentLineups.find { it.name == jsonLineup.name }
                ?: error("No lineup found with such a name ${jsonLineup.name} in db: $currentLineups")
            val teamAVehicleIds = currentXrefs.filter { it.teamId == lineupEntity.teamAId }
            val teamBVehicleIds = currentXrefs.filter { it.teamId == lineupEntity.teamBId }

            // mark vehicles to add, and vehicles to delete
            val toInsert = listOf(
                findNewVehicles.process(
                    jsonLineup.jsonTeamA,
                    teamAVehicleIds,
                    lineupEntity.teamAId
                ),
                findNewVehicles.process(
                    jsonLineup.jsonTeamB,
                    teamBVehicleIds,
                    lineupEntity.teamBId
                )
            ).flatten()

            //reverse search teamAVehicle in a json teamA, to match vehicles that was removed
            val toUpdate = listOf(
                findVehiclesToDelete.process(
                    jsonLineup.jsonTeamA,
                    teamAVehicleIds
                ),
                findVehiclesToDelete.process(
                    jsonLineup.jsonTeamB,
                    teamBVehicleIds
                )
            ).flatten()
            if (toInsert.isNotEmpty())
                dao.insertTeamWithVehicleCrossRef(toInsert)
            if (toUpdate.isNotEmpty())
                dao.updateVehicleCrossRef(toUpdate)
        }
    }

}