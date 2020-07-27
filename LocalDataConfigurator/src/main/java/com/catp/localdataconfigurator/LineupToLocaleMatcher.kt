package com.catp.localdataconfigurator

import com.catp.model.JsonLineup
import com.catp.model.JsonLocaleItem
import com.catp.model.JsonVehicle
import com.catp.model.JsonVehicleStore

// File is used to merge data from old csv file(1.93) with lineups and wpcost/unitidlocale together, will not used after new spreed sheet generator created
class LineupToLocaleMatcher(
    val lineups: List<JsonLineup>,
    private val wpCost: WPCost,
    private val unitIDLocale: UnitIDLocale,
    private val vehicleStore: JsonVehicleStore
) {


    fun process() {
        unitIDLocale.extractNationField(wpCost)
        matchLocales()
        matchWPCost()
        fillVehicleStore()
    }

    private fun fillVehicleStore() {
        wpCost.vehicleItems.values/*.filter { it.unitClass == VehicleType.PLANE }*/.forEach { item ->
            val localeItem = unitIDLocale.localeData.values.find { it.id == item.id }
            if (localeItem == null) {
                println("Can't find locale for id $item tipicaly this happens cause unitIdLocale ignores some of the units with _race or _tutorial suffix, but wpcost is not, in most case, its not a problem")
            } else {
                //Changed localeItem.englishTitle to localeItem.id cause it makes bugs in futher spreedsheetgenerator
                vehicleStore.vehicleList.add(
                    JsonVehicle(
                        localeItem.id,
                        item.unitClass,
                        item.country,
                        item.br,
                        localeItem
                    )
                )
            }
        }
    }

    private fun matchWPCost() {
        //Match by id with real nation, and unit type
        lineups.forEach { lineup ->
            for (team in listOf(lineup.jsonTeamA, lineup.jsonTeamB)) {
                for (vehicle in team.vehicles) {
                    val id = vehicle.locale!!.id

                    val vehicleItem = wpCost.vehicleItems[id]
                    if (vehicleItem == null) {
                        println("$id not found in wpcost file for $vehicle")
                    } else {
                        /*if (vehicle.br != vehicleItem.br) {
                            //Ignored this, cause actual br would be taken from wpcost and overwrited
                            //println("$vehicle have different BR - ${vehicleItem.br}")
                        }*/
                        if (vehicle.nation != vehicleItem.country) {
                            println("$vehicle have different country - $vehicleItem")
                        }
                    }
                }
            }
        }
    }

    private fun matchLocales() {
        var notFound = 0
        lineups.forEach { lineup ->
            for (team in listOf(lineup.jsonTeamA, lineup.jsonTeamB)) {
                for (vehicle in team.vehicles) {
                    if (vehicle.locale == null) {
                        val locales =
                            findLocaleByVehicleName(vehicle)
                        when (locales.size) {
                            0 -> {
                                println("$notFound Can't find locale for $vehicle")
                                notFound++
                            }
                            1 -> vehicle.locale = locales.first()
                            else -> {
                                guesLocaleByPrefixOrTitleMatch(vehicle, locales)
                            }
                        }

                    }
                }
            }
        }
    }

    private fun guesLocaleByPrefixOrTitleMatch(
        vehicle: JsonVehicle,
        locales: List<JsonLocaleItem>
    ) {
        val list = locales.filter { vehicle.nation == it.nation }
        when (list.size) {
            1 -> vehicle.locale = list.first()
            else ->
                println("Found multiple name matches for $vehicle - [$locales]")
        }
        /*val nationPrefix = when (vehicle.nation) {
            "RU" -> "ussr"
            "GR" -> "germ"
            "UR" -> "uk"
            "US" -> "us"
            "JP" -> "jp"
            "IT" -> "it"
            "FR" -> "fr"
            "CN" -> "cn"
            else -> ""
        }
        var locale = locales.find { it.id.startsWith(nationPrefix) }
        when {
            locale != null -> {
                vehicle.locale = locale
                //println("Multiple locale set with nation prefix for: $vehicle $locale")
            }
            locales.find { it.nation == vehicle.nation } -> {
                vehicle.locale = locales[0]
                //println("Multiple locale set with first value: $vehicle ${vehicle.locale}")
            }
            else -> {
                println("Found multiple name matches for $vehicle - [$locales]")
            }
        }*/
    }

    private fun findLocaleByVehicleName(vehicle: JsonVehicle): List<JsonLocaleItem> {
        var locales =
            unitIDLocale.localeData.values.filter { item -> item.exactMatch(vehicle.name) }

        if (locales.isEmpty()) {
            //Trying to find content using similar text find
            locales =
                unitIDLocale.localeData.values.filter { item ->
                    item.similarMatch(vehicle.name)
                }
            //println("No exact match for $vehicle - [$locales]")
        }
        return locales
    }

}