package com.catp.localdataconfigurator

import com.catp.model.*
import com.dslplatform.json.DslJson
import com.dslplatform.json.PrettifyOutputStream
import com.dslplatform.json.runtime.Settings
import java.io.ByteArrayOutputStream

/***
 * Loads actual BR data from https://github.com/VitaliiAndreev/WarThunder_JsonFileChanges wpcost
 * Loads vehicle id to locale names from unit.csv
 * Tooks local spreed sheet with actual lineup to vehicle relations
 * produces json file with actual rules of lineups view
 */


fun generateFromOldCSV() {
    val unitIDLocale = UnitIDLocale().apply { loadData() }
    val wpCost = WPCost().apply { loadData() }
    val lineups = OldSpreedSheetLoader().run {
        load()
        lineupList
    }
    val vehicleStore = JsonVehicleStore(mutableListOf())
    LineupToLocaleMatcher(lineups.map { it.lineup }, wpCost, unitIDLocale, vehicleStore).process()
    SpreedSheetGenerator(lineups.map { it.lineup }, vehicleStore).make()
}


val json = DslJson(Settings.withRuntime<Any>().includeServiceLoader().allowArrayFormat(true))

fun generateJSONFromXLSX() {

}

fun printSome() {
    val localeItem = JsonLocaleItem(
        "MYVEHICLE",
        "MYVEHICLE title",
        "MYVEHICLE title",
        "MYVEHICLE title",
        "MYVEHICLE title",
        "RU"
    )
    vehicleStore.vehicleList.add(
        JsonVehicle(
            "MYVEHICLE",
            VehicleType.TANK,
            "RU",
            "1.1",
            localeItem
        )
    )
    val lineup = JsonLineup("1_1")
    lineup.jsonTeamA.vehicleIdList.add("MYVEHICLE")
    val baos = ByteArrayOutputStream()
    json.serialize(
        JsonLineupConfig(listOf(lineup), vehicleStore, JsonRules()),
        PrettifyOutputStream(baos)
    )
    println("🔥")
    println(baos)
}

