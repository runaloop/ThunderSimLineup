package com.catp.thundersimlineup.ui.lineuplist

import android.content.Context
import com.catp.model.VehicleType
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.data.db.entity.Lineup
import com.catp.thundersimlineup.data.db.entity.Team
import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.catp.thundersimlineup.ui.vehiclelist.VehicleItem
import com.mikepenz.fastadapter.adapters.ItemAdapter

class LineupAdapter : ItemAdapter<VehicleItem>() {

    var originalData: List<Lineup> = emptyList()
    lateinit var filters: LineupListViewModel.FilterState

    fun setNewLineup(context: Context, lineup: LineupRequestInteractor.LineupForToday) {
        originalData = listOfNotNull(
            lineup.lineupNow.first,
            lineup.lineupNow.second,
            lineup.lineupThen.first,
            lineup.lineupThen.second
        )
        val dataset = DataSetCreator(context).make(originalData, filters)
        set(dataset)

    }

    fun setFilterState(context: Context, filters: LineupListViewModel.FilterState) {
        this.filters = filters
        set(DataSetCreator(context).make(originalData, filters))
    }
}

//Takes list of Lineups, fills it with view items: Titles like commands, vehicle type titles, vehicle sorted by type/favorite mode etc
class DataSetCreator(val context: Context) {
    fun make(lineups: List<Lineup>, filters: LineupListViewModel.FilterState): List<VehicleItem> {
        val data = mutableListOf<VehicleItem>()

        lineups.forEachIndexed { index, lineup ->
            if (index < 2 && filters.nowLineupShow || index > 1 && filters.laterLineupShow || lineups.size == 2)
                fillSet(lineup, data, filters)
        }
        return data
    }

    private fun fillSet(
        lineup: Lineup,
        dataset: MutableList<VehicleItem>,
        filters: LineupListViewModel.FilterState
    ) {

        val teams = mapOf(
            lineup.teamA to if (filters.teamAShow) context.getString(R.string.team_a_title) else null,
            lineup.teamB to if (filters.teamBShow) context.getString(R.string.team_b_title) else null
        )
        teams.keys.forEach { team ->
            if (teams[team] != null)
                fillTeam(team, dataset, filters, "${lineup.lineupEntity.name} ${teams[team]}")
        }
    }

    private fun fillTeam(
        team: Team,
        dataset: MutableList<VehicleItem>,
        filters: LineupListViewModel.FilterState,
        title: String
    ) {
        with(team) {
            val vehicles = mapOf(
                VehicleType.TANK to if (filters.tanksShow) context.getString(R.string.tanks_title) else null,
                VehicleType.PLANE to if (filters.planesShow) context.getString(R.string.planes_title) else null,
                VehicleType.HELI to if (filters.helisShow) context.getString(R.string.helis_title) else null
            )
            team.vehicles.groupBy { it.type }.forEach { (type, list) ->
                if (list.isNotEmpty() && vehicles[type] != null) {

                    val isLowLineup = title.indexOf("_1") != -1
                    if (isLowLineup && filters.lowLineupShow || (!isLowLineup && filters.highLineupShow)) {
                        val header = "$title ${vehicles[type]}"
                        fillVehicleList(list, dataset, header)
                    }
                }
            }
        }
    }


    private fun fillVehicleList(
        vehicleList: List<Vehicle>,
        dataset: MutableList<VehicleItem>,
        header: String
    ) {
        vehicleList
            .sortedBy { it.isFavorite }
            .sortedBy { it.nation }
            .forEach { vehicle ->
                dataset += VehicleItem(vehicle, header)
            }
    }
}