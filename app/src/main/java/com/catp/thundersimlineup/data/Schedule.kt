package com.catp.thundersimlineup.data

import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.*
import org.threeten.bp.LocalDate
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class Schedule {
    @Inject
    lateinit var dao: LineupDao

    @Inject
    lateinit var getShiftedLineup: GetShiftedLineup

    internal lateinit var lineupsMap: MutableMap<LineupType, List<LineupCycleEntity>>
    internal var lineupAvailability: LineupCycleAvailabilityEntity? = null
    internal lateinit var lineupShift: List<LineupShiftEntity>

    fun updateRule() {
        val lineups = dao.getLineupCycleList()
        lineupsMap = mutableMapOf()
        LineupType.values().forEach { type ->
            lineupsMap[type] = lineups.filter { it.type == type }
        }
        lineupShift = dao.getLineupShift()
        lineupAvailability = dao.getLineupAvailability()
    }

    //Assume we have only one experimental lineup
    fun getExperimentalLineupForDate(date: LocalDate): Lineup? {
        lineupAvailability?.let { lineupAvailability ->
            if (date.isAfter(lineupAvailability.startOfLineup.minusDays(1)) && date.isBefore(lineupAvailability.endOfLineup.plusDays(1))) {
                val lineupName = lineupsMap.values.flatten()
                    .find { lineupAvailability.lineupId == it.id }!!.lineupName
                return dao.getLineups().find { it.lineupEntity.name == lineupName }
            }
        }
        return null
    }

    fun getLineupForDate(date: LocalDate, lineupType: LineupType): Lineup? {
        val lineups = dao.getLineups()
        val lineupCycle =
            lineupsMap[lineupType]!!.find { lineupCycleEntity -> lineupShift.any { it.lineupId == lineupCycleEntity.id } }
                ?: error("Can't find lineup by shift id $lineupShift at $lineups")
        val shift = lineupShift.find { it.lineupId == lineupCycle.id }!!
        val shiftedLineup = getShiftedLineup.process(shift, date, lineupsMap[lineupType]!!)
        return lineups.find { it.lineupEntity.name == shiftedLineup.lineupName }
    }
}

