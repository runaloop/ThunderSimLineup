package com.catp.model

import com.dslplatform.json.CompiledJson

//formats = arrayOf(CompiledJson.Format.ARRAY)
@CompiledJson(formats = [CompiledJson.Format.ARRAY])
data class JsonLineup(
    val name: String,
    val jsonTeamA: JsonTeam = JsonTeam(),
    val jsonTeamB: JsonTeam = JsonTeam()
) {
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


    val fullVehicleList: List<JsonVehicle>
        get() = listOf(jsonTeamA.vehicles, jsonTeamB.vehicles).flatten()

    fun hasVehicle(vehicle: JsonVehicle) =
        jsonTeamA.hasVehicle(vehicle) || jsonTeamB.hasVehicle(vehicle)


    override fun toString(): String {
        return "Lineup(name='$name', teamA=$jsonTeamA, teamB=$jsonTeamB)"
    }
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

    fun removeUglySymbolsFromTitles() {
        //full list - [ , ", ', (, ), *, ,, -, ., /, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, :, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, ª, ä, è, é, ö, ü, К, М, С, Т, а, —, №, ⋠, ␗, ␙, ␠, ▀, ▂, ▃, ▄, ▅, ]
        val toDelete = Regex("[⋠␗␙␠▀▂▃▄▅\uF059]")
        vehicleList.forEach { item ->
            if (item.locale?.title?.contains(toDelete) == true) {
                val old = item.locale!!.title
                val new = item.locale!!.title.replace(toDelete, "")
                println("Old: ${old}\nNew: $new")
                item.locale!!.title = new
            }
        }
    }

    fun removeForbidenIds() {
        val forbiddenIdsEndings = listOf("_football", "us_amx_13_75", "yt_cup_2019", "us_amx_13_90", "yt_cup_2019")
        val forbiddenTypes = listOf(VehicleType.SHIP)
        vehicleList.removeAll { vehicle ->
            vehicle.type in forbiddenTypes || forbiddenIdsEndings.any { vehicle.name.contains(it) }
        }
    }
}


val lineupTitles = listOf(
    "1_1", "2_1", "3_1", "4_1", "5_1", "6_1"
    , "8_2", "8_2_2", "9_2", "10_2", "12_2"
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

