package com.catp.model

import com.dslplatform.json.CompiledJson

//formats = arrayOf(CompiledJson.Format.ARRAY)
@CompiledJson(formats = [CompiledJson.Format.ARRAY])
data class JsonLineup(val name: String, val jsonTeamA: JsonTeam = JsonTeam(), val jsonTeamB: JsonTeam = JsonTeam()) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JsonLineup) return false

        if (name != other.name) return false
        if (!jsonTeamA.equals(other.jsonTeamA)) return false
        if (!jsonTeamB.equals(other.jsonTeamB)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + jsonTeamA.hashCode()
        result = 31 * result + jsonTeamB.hashCode()
        return result
    }

    fun isLineupHighTear() = name.endsWith("_2")

    val fullVehicleList: List<JsonVehicle>
        get() = listOf(jsonTeamA.vehicles, jsonTeamB.vehicles).flatten()

    fun hasVehicle(vehicle: JsonVehicle) = jsonTeamA.hasVehicle(vehicle) || jsonTeamB.hasVehicle(vehicle)


    /**
     * Removes all vehicle from specified lineups 1_1 - 6_1 and adds planes from vehicle store that matches team and BR condition
     */
    fun updateVehiclesFromVehicleStore(vehicleStore: JsonVehicleStore, rules: JsonRules) {
        if (!rules.LINEUP_TO_BR_RELATION.containsKey(name)) return
        listOf(jsonTeamA, jsonTeamB).forEach { team ->
            val planes = team.planes.map { it.name }
            team.vehicleIdList.removeAll { it in planes }
            val brList = rules.LINEUP_TO_BR_RELATION[name]!!

            val nationsInTeam = team.vehicles.map { it.nation }.toSet()
            val vehiclesToAdd = vehicleStore.vehicleList.filter {
                brList.contains(it.br) && nationsInTeam.contains(it.nation)
            }
            team.vehicleIdList.addAll(vehiclesToAdd.map { it.name })
        }
    }


    fun hasChanges(previousLineup: JsonLineup): Boolean {
        return !equals(previousLineup)
    }

    //TODO: mark methods must be moved to room pojo files but now will comment it only
    /*
    fun compareWithPrevious(previousLineup: Lineup) {
        removeDeletedAndNewMarks(previousLineup)
        markNew(previousLineup)
        markDeleted(previousLineup)
    }


    fun removeDeletedAndNewMarks(previousLineup: Lineup) {
        for (team in listOf(previousLineup.teamA, previousLineup.teamB)) {
            with(team) {
                //TODO: need to fix this behaviour
                /*for (list in vehicleList) {
                    list.removeAll { vehicle -> vehicle.state == VehicleState.DELETED }
                    list.filter { vehicle -> vehicle.state == VehicleState.NEW }
                        .forEach { vehicle -> vehicle.state = VehicleState.REGULAR }
                }*/
            }
        }

    }

    fun markDeleted(previousLineup: Lineup) {
        for ((teamNew, teamOld) in mapOf(
            teamA to previousLineup.teamA,
            teamB to previousLineup.teamB
        )) {
            for ((oldList, newList) in mapOf(
                teamOld.vehicles to teamNew.vehicles
            )) {
                subtractAndMarkState(oldList, newList, VehicleState.DELETED)
                oldList
                    .filter { vehicle -> vehicle.state == VehicleState.DELETED }
                    .forEach { newList.add(it) }
            }
        }
    }

    fun markNew(previousLineup: Lineup) {
        for ((teamNew, teamOld) in mapOf(
            teamA to previousLineup.teamA,
            teamB to previousLineup.teamB
        )) {
            for ((oldList, newList) in mapOf(
                teamOld.tanks to teamNew.tanks,
                teamOld.planes to teamNew.planes,
                teamOld.helis to teamNew.helis
            )) {
                subtractAndMarkState(newList, oldList)
            }
        }

    }

    private fun subtractAndMarkState(
        first: Collection<Vehicle>,
        second: Collection<Vehicle>,
        state: VehicleState = VehicleState.NEW
    ) {
        first.subtract(second).forEach { vehicle -> vehicle.state = state }
    }*/

    override fun toString(): String {
        return "Lineup(name='$name', teamA=$jsonTeamA, teamB=$jsonTeamB)"
    }
}

@CompiledJson(formats = [CompiledJson.Format.ARRAY])
data class JsonTeam(val vehicleIdList: MutableList<String> = mutableListOf<String>()) {

    val tanks: List<JsonVehicle>
        get() {
            return vehicleStore.vehicleList.filter { it.type == VehicleType.TANK && it.name in vehicleIdList }
        }
    val planes: List<JsonVehicle>
        get() {
            return vehicleStore.vehicleList.filter { it.type == VehicleType.PLANE && it.name in vehicleIdList }
        }
    val helis: List<JsonVehicle>
        get() {
            return vehicleStore.vehicleList.filter { it.type == VehicleType.HELI && it.name in vehicleIdList }
        }


    val vehicles
        get() =
            vehicleStore.vehicleList.filter { it.name in vehicleIdList }


    fun hasVehicle(vehicle: JsonVehicle): Boolean =
        vehicleIdList.any { it == vehicle.name }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JsonTeam) return false

        if (vehicleIdList != other.vehicleIdList) return false

        return true
    }


    override fun hashCode(): Int {
        return vehicleIdList.hashCode()
    }

    override fun toString(): String {
        return "Team(vehicleList=$vehicleIdList)"
    }
}

val vehicleStore = JsonVehicleStore()

@CompiledJson(formats = [CompiledJson.Format.ARRAY])
class JsonVehicleStore(val vehicleList: MutableList<JsonVehicle> = mutableListOf()) {

    fun getPlanes(br: String): List<JsonVehicle> {
        return vehicleList.filter { it.br == br && it.type == VehicleType.PLANE }
    }

    fun getHelis(br: String): List<JsonVehicle> {
        return vehicleList.filter { it.br == br && it.type == VehicleType.HELI }
    }

    fun getTanks(br: String): List<JsonVehicle> {
        return vehicleList.filter { it.br == br && it.type == VehicleType.TANK }
    }
}


//TODO: Possible need to remove this to Rules class?
val lineupTitles = listOf(
    "1_1", "2_1", "3_1", "4_1", "5_1", "6_1"
    , "8_2", "8_2_2", "9_2", "10_2", "12_2"
)


@CompiledJson(formats = [CompiledJson.Format.ARRAY])
data class JsonVehicle(
    val name: String,
    val type: VehicleType,
    val nation: String = "",
    val br: String = "",
    var locale: JsonLocaleItem? = null
) {
    constructor() : this("", VehicleType.TANK, "", "", null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JsonVehicle) return false

        if (name != other.name) return false
        if (type != other.type) return false
        if (nation != other.nation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + nation.hashCode()
        return result
    }

}

enum class VehicleType {
    TANK, PLANE, HELI, SHIP;
}
enum class VehicleState {
    REGULAR, DELETED, NEW
}

enum class TeamType {
    A, B
}

