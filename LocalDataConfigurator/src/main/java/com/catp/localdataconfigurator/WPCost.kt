package com.catp.localdataconfigurator

import com.beust.klaxon.Klaxon
import com.beust.klaxon.PathMatcher
import com.catp.model.VehicleType
import java.io.StringReader
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.regex.Pattern

class WPCost {
    companion object {
        const val WPCOST_PATH =
            //"https://github.com/VitaliiAndreev/WarThunder_JsonFileChanges/blob/master/Files/char.vromfs.bin_u/config/wpcost.blkx?raw=true"
            //"https://github.com/VitaliiAndreev/WarThunder_JsonFileChanges_DevClient/blob/master/Files/char.vromfs.bin_u/config/wpcost.blkx?raw=true"
            "https://github.com/gszabi99/War-Thunder-Datamine/blob/master/char.vromfs.bin_u/config/wpcost.blkx?raw=true"
    }

    val vehicleItems = mutableMapOf<String, VehicleItem>()
    private val economicRankPattern = "economicRankSimulation"
    private val countryPattern = "country"
    private val unitClassPattern = "unitClass"

    fun loadData(tryLocalFirst: Boolean = true) {
        val data = Loader().load(WPCOST_PATH, tryLocalFirst)
        println("Parsing WPCOST")
        Klaxon()
            .pathMatcher(BRPatchMatcher(economicRankPattern, vehicleItems))
            .pathMatcher(BRPatchMatcher(countryPattern, vehicleItems))
            .pathMatcher(BRPatchMatcher(unitClassPattern, vehicleItems))
            .parseJsonObject(StringReader(data))
        println("Finished")
    }
}

class BRPatchMatcher(private val fieldName: String, private val vehicles: MutableMap<String, VehicleItem>) :
    PathMatcher {

    private val fieldPattern = ".*$fieldName"
    private val pattern = Pattern.compile(fieldPattern)

    private fun extractVehicleId(path: String): String = path.substring(2, path.lastIndexOf('.'))

    override fun onMatch(path: String, value: Any) {
        val id = extractVehicleId(path)
        if (!vehicles.containsKey(id)) {
            vehicles[id] = VehicleItem(id)
        }
        vehicles[id]!!.params[fieldName] = value
    }

    override fun pathMatches(path: String): Boolean {
        return pattern.matcher(path).matches()
    }

}

data class VehicleItem(val id: String) {

    val params = mutableMapOf<String, Any>()

    val br: String
        get() {
            val n = (1 + economicRankSimulation / 3.0)
            val df = DecimalFormat("#.#").also {
                it.roundingMode = RoundingMode.HALF_DOWN
                it.minimumFractionDigits = 1
            }
            return df.format(n)
        }


    private val economicRankSimulation: Int
        get() = Integer.valueOf(params["economicRankSimulation"].toString())
    val country: String
        get() = countryMap[params["country"].toString()]
            ?: error("Unknown country: ${params["country"]}")
    val unitClass: VehicleType
        get() = vehicleType[params["unitClass"].toString()]
            ?: error("Unknown vehicle type: ${params["unitClass"]}")

    override fun toString(): String {
        return "VehicleItem(id='$id', params=$params)"
    }

    companion object {
        val countryMap = mapOf(
            "country_italy" to "IT",
            "country_france" to "FR",
            "country_britain" to "UK",
            "country_usa" to "US",
            "country_ussr" to "RU",
            "country_china" to "CH",
            "country_japan" to "JP",
            "country_germany" to "GR",
            "country_sweden" to "SW"
        )
        val vehicleType = mapOf(
            "exp_cruiser" to VehicleType.SHIP,
            "exp_gun_boat" to VehicleType.SHIP,
            "exp_naval_ferry_barge" to VehicleType.SHIP,
            "exp_submarine_chaser" to VehicleType.SHIP,
            "exp_torpedo_boat" to VehicleType.SHIP,
            "exp_torpedo_gun_boat" to VehicleType.SHIP,
            "exp_destroyer" to VehicleType.SHIP,
            "exp_SPAA" to VehicleType.TANK,
            "exp_assault" to VehicleType.PLANE,
            "exp_bomber" to VehicleType.PLANE,
            "exp_fighter" to VehicleType.PLANE,
            "exp_helicopter" to VehicleType.HELI,
            "exp_tank" to VehicleType.TANK,
            "exp_heavy_tank" to VehicleType.TANK,
            "exp_tank_destroyer" to VehicleType.TANK
        )
    }


}