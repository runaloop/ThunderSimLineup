package com.catp.thundersimlineup.data

import com.catp.thundersimlineup.data.db.entity.LineupCycleEntity
import com.catp.thundersimlineup.data.db.entity.LineupShiftEntity
import com.catp.thundersimlineup.lShift
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit
import toothpick.InjectConstructor

@InjectConstructor
class GetShiftedLineup {
    fun process(
        shift: LineupShiftEntity,
        date: LocalDate,
        lineups: List<LineupCycleEntity>
    ): LineupCycleEntity {

        val between = ChronoUnit.DAYS.between(shift.shiftDate, date).toInt()

        val order = lineups.indexOfFirst { it.id == shift.lineupId }
        if (order == -1) error("Cant find correct shift lineup id: $shift in $lineups")

        val data = lineups.lShift(order + between)
        return data.first()
    }
}