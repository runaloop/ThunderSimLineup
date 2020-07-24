package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonLineup
import com.catp.thundersimlineup.data.db.Changeset
import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.LineupEntity
import com.catp.thundersimlineup.data.db.entity.TeamEntity
import toothpick.InjectConstructor

//Create pair of team for each new lineup, and returns map with teamtable id, for each lineup in JsonLineup
@InjectConstructor
class UpdateTeams(val dao: LineupDao, val changeset: Changeset) {
    fun process(jsonLineups: List<JsonLineup>) {
        //Find out what teams and lineups need to be inserted
        val allLineups = dao.getLineupsEntity().map { it.name }
        val lineupsToAdd = jsonLineups
            .filter { lineup -> !allLineups.contains(lineup.name) }
        val teamsToAdd = lineupsToAdd
            .flatMap { jsonLineup ->
                listOf(TeamEntity(jsonLineup.name, "A"), TeamEntity(jsonLineup.name, "B"))
            }
        //Insert new teams
        if (teamsToAdd.isNotEmpty()) {
            dao.insertTeams(teamsToAdd)
            //prepare lineups to add
            val allTeams = dao.getTeamTable()
            val newLineups = lineupsToAdd.map { jsonLineup ->
                LineupEntity(
                    jsonLineup.name,
                    allTeams.find { it.teamLetter == "A" && it.lineupName == jsonLineup.name }!!.teamId,
                    allTeams.find { it.teamLetter == "B" && it.lineupName == jsonLineup.name }!!.teamId
                )
            }
            //Insert new lineups
            dao.insertLineups(newLineups)
        }
    }
}