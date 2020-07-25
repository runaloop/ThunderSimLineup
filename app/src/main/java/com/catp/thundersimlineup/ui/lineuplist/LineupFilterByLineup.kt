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
            true,
            true,
            vehicles.any { it.type == VehicleType.TANK } && prefFilter.tanksShow,
            vehicles.any { it.type == VehicleType.PLANE } && prefFilter.planesShow,
            vehicles.any { it.type == VehicleType.HELI } &&   prefFilter.helisShow,
            prefFilter.lowLineupShow, prefFilter.highLineupShow,
            lineupForToday.isLineupForNow && prefFilter.nowLineupShow, lineupForToday.isLineupForNow && prefFilter.laterLineupShow
        )
    }

}
