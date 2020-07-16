package com.catp.thundersimlineup.ui.lineuplist

import com.catp.model.VehicleType
import toothpick.InjectConstructor

@InjectConstructor
class LineupFilterByLineup {
    fun getFilters(lineupForToday: LineupRequestInteractor.LineupForToday): LineupListViewModel.FilterState {
        val vehicles =
            listOf(lineupForToday.lineupNow.toList(), lineupForToday.lineupThen.toList()).flatten()
                .filterNotNull().map { listOf(it.teamA.vehicles, it.teamB.vehicles).flatten() }
                .flatten()
        return LineupListViewModel.FilterState(
            "",
            true,
            true,
            vehicles.any { it.type == VehicleType.TANK },
            vehicles.any { it.type == VehicleType.PLANE },
            vehicles.any { it.type == VehicleType.HELI },
            true, true,
            lineupForToday.isLineupForNow, lineupForToday.isLineupForNow
        )
    }

}
