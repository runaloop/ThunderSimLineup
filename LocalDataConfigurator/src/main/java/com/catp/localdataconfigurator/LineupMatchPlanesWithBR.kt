package com.catp.localdataconfigurator

import com.catp.model.*

class LineupMatchPlanesWithBR(val lineups: List<JsonLineup>, private val vehicleStore: JsonVehicleStore) {
    fun process() {
        val lineupToBr = JsonRules().LINEUP_TO_BR_RELATION
        lineups
            .filter { it.name.endsWith("_1") }
            .forEach { lineup ->
                val brs = lineupToBr[lineup.name]
                if(null != brs){
                    removePlanes(lineup.jsonTeamA)
                    removePlanes(lineup.jsonTeamB)
                    addPlanes(lineup.jsonTeamA, brs)
                    addPlanes(lineup.jsonTeamB, brs)
                }
            }
    }

    private fun addPlanes(team: JsonTeam, brs: List<String>) {
        val nations = listNations(team)
        team.vehicleIdList.addAll(vehicleStore.vehicleList.filter { it.type == VehicleType.PLANE && it.nation in nations && it.BR in brs  }
            .map { it.name })
    }

    private fun listNations(team: JsonTeam) =
        team.vehicles.map { it.nation }.distinct()

    fun removePlanes(team: JsonTeam) {
        val planes = team.planes.map { it.name }
        team.vehicleIdList.removeIf { it in planes }
    }

}
