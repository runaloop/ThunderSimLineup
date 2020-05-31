package com.catp.thundersimlineup.data

import com.catp.thundersimlineup.data.db.entity.LineupCycleEntity
import com.catp.thundersimlineup.data.db.entity.LineupShiftEntity
import com.catp.thundersimlineup.lShift
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import toothpick.InjectConstructor
import kotlin.math.absoluteValue

@InjectConstructor
class GetShiftedLineup {
    fun process(
        shift: LineupShiftEntity,
        date: LocalDate,
        lineups: List<LineupCycleEntity>
    ): LineupCycleEntity {
        val between = Period.between(shift.shiftDate, date)
        val daysToShift = between.days.absoluteValue
        val order = lineups.find { it.id == shift.lineupId }?.orderNumber
            ?: error("Cant find correct shift lineup id: $shift in $lineups")
        val data = lineups.lShift(order + daysToShift)
        return data.first()
    }
}