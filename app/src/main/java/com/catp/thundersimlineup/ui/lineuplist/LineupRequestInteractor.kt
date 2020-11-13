package com.catp.thundersimlineup.ui.lineuplist

import com.catp.model.JsonRules.Companion.LINEUP_UTC_TIME_OF_CHANGE
import com.catp.thundersimlineup.LocalDateTimeProvider
import com.catp.thundersimlineup.data.Schedule
import com.catp.thundersimlineup.data.db.entity.Lineup
import com.catp.thundersimlineup.data.db.entity.LineupType
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class LineupRequestInteractor {

    @Inject
    lateinit var lineupSchedule: Schedule

    @Inject
    lateinit var localDateTimeProvider: LocalDateTimeProvider

    fun getLineupForADay(day: LocalDate): LineupForToday {
        //Get current time, if its before lineup change, asks previous day lineups
        //If its after lineup change, asks today lineup
        //If the day is not today, returns exact day
        lineupSchedule.updateRule()

        val currentUTC = localDateTimeProvider.now()
        val nextLineupUTC =
            localDateTimeProvider.now().withHour(LINEUP_UTC_TIME_OF_CHANGE)
                .withMinute(0).withSecond(0)

        val dayToLoad: LocalDate
        val diff: Duration
        if (currentUTC.hour < LINEUP_UTC_TIME_OF_CHANGE && currentUTC.toLocalDate() == day) {
            dayToLoad = currentUTC.toLocalDate().minusDays(1)
            diff = Duration.between(currentUTC, nextLineupUTC)
        } else {
            dayToLoad = day
            diff = if (currentUTC.toLocalDate() != day) Duration.ZERO else {
                Duration.between(
                    currentUTC,
                    nextLineupUTC.plusDays(1)
                )
            }
        }



        val isLineupForToday = !diff.isZero
        return LineupForToday(
            Pair(
                lineupSchedule.getLineupForDate(dayToLoad, LineupType.LOW),
                lineupSchedule.getLineupForDate(dayToLoad, LineupType.TOP)
            ),
            if (isLineupForToday)
                Pair(
                    lineupSchedule.getLineupForDate(dayToLoad.plusDays(1), LineupType.LOW),
                    lineupSchedule.getLineupForDate(dayToLoad.plusDays(1), LineupType.TOP)
                ) else Pair(null, null)
            ,
            diff,
            isLineupForToday,
            currentUTC.toLocalDate().isAfter(day)
        )
    }

    data class LineupForToday(
        val lineupNow: Pair<Lineup?, Lineup?>,
        val lineupThen: Pair<Lineup?, Lineup?>,
        val timeToChange: Duration,
        val isLineupForNow: Boolean = false,
        val isPast: Boolean = false
    )
}