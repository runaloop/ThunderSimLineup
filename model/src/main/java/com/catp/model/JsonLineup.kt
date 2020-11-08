package com.catp.model

import com.dslplatform.json.CompiledJson

@CompiledJson(formats = [CompiledJson.Format.ARRAY])
data class JsonLineup(
    val name: String,
    val jsonTeamA: JsonTeam = JsonTeam(),
    val jsonTeamB: JsonTeam = JsonTeam()
) {
    val fullVehicleList: List<JsonVehicle>
        get() = listOf(jsonTeamA.vehicles, jsonTeamB.vehicles).flatten()

    fun hasVehicle(vehicle: JsonVehicle) =
        jsonTeamA.hasVehicle(vehicle) || jsonTeamB.hasVehicle(vehicle)
}

@CompiledJson(formats = [CompiledJson.Format.ARRAY])
data class JsonTeam(val vehicleIdList: MutableList<String> = mutableListOf()) {

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


val lineupTitles = listOf(
    "1_1", "2_1", "3_1", "4_1", "5_1", "6_1", "8_2", "8_2_2", "9_2", "10_2", "12_2"
)


@CompiledJson(formats = [CompiledJson.Format.ARRAY])
data class JsonVehicle(
    val name: String,
    val type: VehicleType,
    val nation: String = "",
    var br: String = "",
    var locale: JsonLocaleItem? = null
) {
    constructor() : this("", VehicleType.TANK, "", "", null)

    /**
     * Equals method used in a data class, because for business logic vehicle is still equal even
     * if BR, or locale are changed, and there is no way to move BR, and locale out of the constructor,
     * cuase it crashes dsljson generated classes.
     */

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JsonVehicle) return false

        if (name != other.name) return false
        if (type != other.type) return false
        if (nation != other.nation) return false

        return true
    }

    /**
     * Same reason as for equals method.
     */
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

