package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonRules
import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.*
import org.threeten.bp.LocalDate
import toothpick.InjectConstructor

@InjectConstructor
class UpdateLineupCycle(val dao: LineupDao) {
    fun process(jsonRule: JsonRules) {
        dao.deleteLineupShift()
        dao.deleteLineupAvailability()
        dao.deleteLineupToBr()
        dao.deleteLineupCycleList()

        val lineups = dao.getLineupsEntity()

        with(jsonRule) {
            dao.insertLineupCycleList(
                listOf(
                    jsonLineupToLineupCycle(lineupCycleRule1, LineupType.LOW, LINEUP_TO_BR_RELATION),
                    jsonLineupToLineupCycle(lineupCycleRule2, LineupType.TOP, LINEUP_TO_BR_RELATION),
                    jsonLineupToLineupCycle(lineupCycleRuleExcremental, LineupType.EXCREMENTAL, LINEUP_TO_BR_RELATION)
                ).flatten()
            )
            val lineupCycleList = dao.getLineupCycleList()

            //insert toBr
            val brToInsert = LINEUP_TO_BR_RELATION.keys.flatMap { lineupName ->
                val lineupCycle = lineupCycleList.find { it.lineupName == lineupName }
                    ?: error("Can't find lineup with such a name in db")
                LINEUP_TO_BR_RELATION[lineupName]!!.map {
                    LineupToBREntity(
                        lineupCycle.id,
                        it.toDouble()
                    )
                }
            }
            if (brToInsert.isNotEmpty())
                dao.insertLineupToBREntity(brToInsert)

            if (lineupAvailability.isNotEmpty()) {
                val availabilityToInsert = lineupAvailability.keys.map { name ->
                    val lineupCycle = lineupCycleList.find { it.lineupName == name }
                        ?: error("Can't find lineup with such a name in db")
                    //TODO: Should we crash in such case? or just skip it?
                    LineupCycleAvailabilityEntity(
                        lineupCycle.id,
                        LocalDate.parse(lineupAvailability[name]!!.first),
                        LocalDate.parse(lineupAvailability[name]!!.second)
                    )
                }
                dao.insertLineupCycleAvailability(availabilityToInsert)
            }

            val shift = listOf(
                addShift(lineupShiftRule1, lineupCycleList),
                addShift(lineupShiftRule2, lineupCycleList)
            ).flatten()
            dao.insertLineupCycleShift(shift)
        }
        //Insert all of them
    }

    private fun jsonLineupToLineupCycle(
        lineupCycleRule: List<String>,
        type: LineupType,
        lineupToBrRelation: Map<String, List<String>>
    ): List<LineupCycleEntity> {
        return lineupCycleRule.mapIndexed { index: Int, name: String ->
            LineupCycleEntity(
                name,
                type,
                index,
                lineupToBrRelation.keys.any { it == name }
            )
        }
    }

    fun addShift(
        lineupShiftRule: Map<String, String>,
        lineupCycleList: List<LineupCycleEntity>
    ): List<LineupShiftEntity> {
        return lineupShiftRule.entries.map { (date, name) ->
            val lineupCycle = lineupCycleList.find { it.lineupName == name }
                ?: error("Can't find lineup with such a name in db")
            LineupShiftEntity(lineupCycle.id, LocalDate.parse(date))
        }

    }
}