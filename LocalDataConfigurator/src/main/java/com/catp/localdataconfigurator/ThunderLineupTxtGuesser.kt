package com.catp.localdataconfigurator

import com.beust.klaxon.Klaxon
import com.catp.model.JsonLineup
import com.catp.model.JsonTeam
import com.catp.model.JsonVehicle
import com.catp.model.vehicleStore
import com.github.ajalt.clikt.output.TermUi

//
/**
 Getting list of list of strings, and tries to guess what is lineup low/high and the name of a lineup, than modify lineup
 */

class ThunderLineupTxtGuesser {
    fun parse(data: List<List<String>>) {
        val lineupsFromSpreedSheet = SpreedSheetReader(vehicleStore).read()
        data.forEach { list->
            TermUi.echo("🐷List of items")
            TermUi.echo(list)
            guessAndMerge(lineupsFromSpreedSheet, parseJson(list)!!)
        }
        if (TermUi.confirm("Would you like to generate new xlsx?") == true) {
            SpreedSheetGenerator(lineupsFromSpreedSheet, vehicleStore).make()
        }
    }

    private fun guessAndMerge(
        lineupsFromSpreedSheet: List<JsonLineup>,
        tear: List<TxtLineupItem>
    ) {
        val lineupName = guessLineup(lineupsFromSpreedSheet, tear)
        if (lineupName.isNotEmpty() && lineupName != "-") {
            mergeVehicleToLineup(lineupsFromSpreedSheet, lineupName, tear)
        } else {
            TermUi.echo("Lineup will not be changed")
        }
    }

    private fun mergeVehicleToLineup(
        lineupsFromSpreedSheet: List<JsonLineup>,
        lineupName: String,
        listOfVehicles: List<TxtLineupItem>
    ) {
        val vehiclesFromVehicleStore =
            listOfVehicles.map { item ->
                val vehicle = vehicleStore.vehicleList.find { it.name == item.id }
                if (vehicle == null) {
                    TermUi.echo("\u001B[31mVehicle with id ${item.id} not found in vehicle store!!!!, make sure, you have updated spreedsheet with all new vehicles in a txt file using \u001B[32mRegenerateXLSXFile command")
                    return
                } else
                    return@map vehicle!!
            }
        val lineup = lineupsFromSpreedSheet.find { it.name == lineupName }!!
        var middle = findMiddleTeam(lineupName, vehiclesFromVehicleStore)
        //Find middle item to separate A team from B team(this need to be added, cause of some time experement lineups, when nato contry like germany added to A team)
        if (TermUi.confirm("Confirm first vehicle of B team is: ${listOfVehicles[middle]}") == false) {
            val id = TermUi.prompt("Enter the first vehicle of B team ID:")!!
            middle =
                vehiclesFromVehicleStore.indexOf(vehiclesFromVehicleStore.find { it.name == id })
        }

        val teamANewList = vehiclesFromVehicleStore.subList(0, middle)
        val teamBNewList = vehiclesFromVehicleStore.subList(middle, vehiclesFromVehicleStore.size)

        AddAndRemoveFromTeam(teamANewList, lineup.jsonTeamA, "A")
        AddAndRemoveFromTeam(teamBNewList, lineup.jsonTeamB, "B")
        TermUi.echo("Lineup ${lineup.name} has been changed")
    }

    private fun AddAndRemoveFromTeam(
        teamNewList: List<JsonVehicle>,
        jsonTeam: JsonTeam,
        teamLetter: String
    ) {
        val toDelete = jsonTeam.vehicles.minus(teamNewList)
        val toAdd = teamNewList.minus(jsonTeam.vehicles)

        if (toDelete.isNotEmpty()) {
            if (TermUi.confirm(
                    "Confirm items to remove from team ${teamLetter}: \u001b[31m${toDelete.joinToString(
                        "\n",
                        "\n",
                        "\n"
                    )} \u001B[37m(If not, you will be asked about every listed items separetly)"
                ) == true
            ) {
                toDelete.forEach { item ->
                    jsonTeam.vehicleIdList.remove(item.name)
                }
            } else {
                toDelete.forEach { item ->
                    if (TermUi.confirm("\u001b[37mConfirm remove from team ${teamLetter}: \u001b[31m${item.name} \u001B[37m(If not, it will just be skipped)") == true) {
                        jsonTeam.vehicleIdList.remove(item.name)
                    }
                }
            }
        }
        if(toAdd.isNotEmpty()){
            if (TermUi.confirm(
                    "\u001b[37mConfirm items to add to team $teamLetter \u001b[32m${toAdd.joinToString(
                        "\n",
                        "\n",
                        "\n"
                    )} \u001B[37m(If not, you will be asked about every listed items separetly)"
                ) == true
            ) {
                toAdd.forEach { item ->
                    jsonTeam.vehicleIdList.add(item.name)
                }
            } else {
                toAdd.forEach { item ->
                    if (TermUi.confirm("\u001b[37mConfirm add to team ${teamLetter}: \u001b[32m${item.name} \u001b[37m(If not, it will just be skipped)") == true) {
                        jsonTeam.vehicleIdList.add(item.name)
                    }
                }
            }
        }
    }

    private fun findMiddleTeam(
        lineupName: String,
        listOfVehicles: List<JsonVehicle>
    ): Int {
        val teamACountries = if (lineupName.endsWith("_2")) HIGH_TEAR_A_TEAM_COUNTRIES else LOW_TEAR_A_TEAM_COUNTRIES
        listOfVehicles.forEachIndexed { index, jsonVehicle ->
            if (!teamACountries.contains(jsonVehicle.nation))
                return index
        }
        return 0
    }



    private fun guessLineup(
        lineupsFromSpreedSheet: List<JsonLineup>,
        tear: List<TxtLineupItem>
    ): String {
        val lineupToChange = mutableMapOf<String, Int>()
        val lineupToRemoved = mutableMapOf<String, Set<String>>()
        val lineupToAdded = mutableMapOf<String, Set<String>>()
        lineupsFromSpreedSheet.forEach { lineup ->
            val wasRemoved = getRemovedItems(tear, lineup)
            val wasAdded = getAddedItems(tear, lineup)
            lineupToChange[lineup.name] = wasRemoved.size + wasAdded.size
            lineupToAdded[lineup.name] = wasAdded
            lineupToRemoved[lineup.name] = wasRemoved
        }

        val sorted = lineupToChange.keys.sortedBy { lineupToChange[it] }
        sorted.forEach { name ->
            TermUi.echo("$name have ${lineupToRemoved[name]!!.size} removed, and ${lineupToAdded[name]!!.size} added")
        }

        if (lineupToAdded[sorted.first()]!!.isEmpty() && lineupToRemoved[sorted.first()]!!.isEmpty()) {
            TermUi.echo("Lineup ${sorted.first()} have no change")
            return ""
        }

        return if (TermUi.confirm("Seems that ${sorted.first()} is lineup that you are trying to change?") == true) {
            sorted.first()
        } else {
            TermUi.prompt("Enter lineup where I should insert new items, or type \"-\" if you dont want to change any lineup on this tire")!!
        }
    }


    private fun getAddedItems(
        tear: List<TxtLineupItem>,
        lineup: JsonLineup
    ): Set<String> {
        val wasAdded = tear.map { it.id }.toSet().minus(lineup.fullVehicleList.map { it.name })
        return wasAdded
    }

    private fun getRemovedItems(
        tear: List<TxtLineupItem>,
        lineup: JsonLineup
    ): Set<String> {
        val wasRemoved =
            lineup.fullVehicleList.map { it.name }.toSet().minus(tear.map { it.id })
        return wasRemoved
    }

    fun parseJson(jsonArray: List<String>): List<TxtLineupItem>? {
        return Klaxon().parseArray<TxtLineupItem>(jsonArray.joinToString(",", "[", "]"))
    }

    companion object {
        val HIGH_TEAR_MARKER = "High tear command A"
        val LOW_TEAR_MARKER = "Low tear command A"
        val HIGH_TEAR_A_TEAM_COUNTRIES = listOf("RU", "CH")
        val LOW_TEAR_A_TEAM_COUNTRIES = listOf("RU", "CH", "FR", "UK", "US", "SW")
    }

    class TxtLineupItem(
        val id: String,
        val needShopInfo: Boolean,
        val ttype: String

    ) {
        override fun toString(): String {
            return "id='$id'"
        }
    }


}