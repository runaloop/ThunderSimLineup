package com.catp.model


const val COMMAND_B_MARKER = "Команда Б"

class LineupConfigurator(val name: String, val store: JsonVehicleStore) {
    var currentTeam = TeamType.A
    var currentVehicleType = VehicleType.TANK
    val lineup = JsonLineup(name)
    val team = mapOf(TeamType.A to lineup.jsonTeamA, TeamType.B to lineup.jsonTeamB)



    fun switchTeam() {
        if(currentTeam == TeamType.B)
            throw IllegalStateException("Command B already switched, not possible to switch more")
        currentTeam = TeamType.B
        currentVehicleType = VehicleType.TANK
    }

    fun switchVehicleType() {
        val nextType = currentVehicleType.ordinal + if (name == "12_2") 2 else 1
        if (nextType < VehicleType.values().size)
            currentVehicleType = VehicleType.values()[nextType]
    }

    //@Deprecated - this method should not be used any more, cause it used by old csv sheet loader only
    fun addVehicle(name: String, nation: String = "", br: String = "") {
        /*team[currentTeam]!!.vehicleIdList.add(
            Vehicle(
                name,
                currentVehicleType,
                VehicleState.REGULAR,
                nation, br
            )
        )*/
    }



}