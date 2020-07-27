package com.catp.thundersimlineup.ui.lineuplist

import com.catp.model.VehicleType
import com.catp.thundersimlineup.data.FilterState
import com.catp.thundersimlineup.data.Preferences
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class LineupFilterByLineup {
    @Inject
    lateinit var preferences: Preferences

    fun getFilters(lineupForToday: LineupRequestInteractor.LineupForToday): FilterState {
        val vehicles =
            listOf(lineupForToday.lineupNow.toList(), lineupForToday.lineupThen.toList()).flatten()
                .filterNotNull().map { listOf(it.teamA.vehicles, it.teamB.vehicles).flatten() }
                .flatten()
        val prefFilter = preferences.lineupListFilter
        return FilterState(
            "",
            teamAShow = true,
            teamBShow = true,
            tanksShow = vehicles.any { it.type == VehicleType.TANK } && prefFilter.tanksShow,
            planesShow = vehicles.any { it.type == VehicleType.PLANE } && prefFilter.planesShow,
            helisShow = vehicles.any { it.type == VehicleType.HELI } && prefFilter.helisShow,
            lowLineupShow = prefFilter.lowLineupShow,
            highLineupShow = prefFilter.highLineupShow,
            nowLineupShow = lineupForToday.isLineupForNow && prefFilter.nowLineupShow,
            laterLineupShow = lineupForToday.isLineupForNow && prefFilter.laterLineupShow
        )
    }

}
