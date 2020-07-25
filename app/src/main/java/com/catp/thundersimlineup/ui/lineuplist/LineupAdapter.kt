package com.catp.thundersimlineup.ui.lineuplist

import android.content.Context
import com.catp.model.VehicleType
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.data.FilterState
import com.catp.thundersimlineup.data.db.entity.Lineup
import com.catp.thundersimlineup.data.db.entity.Team
import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.catp.thundersimlineup.ui.list.ExpandableHeaderItem
import com.catp.thundersimlineup.ui.list.HeaderColors
import com.catp.thundersimlineup.ui.list.VehicleItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem

class LineupAdapter :
    FlexibleAdapter<AbstractFlexibleItem<*>>(null, null, true) {

    var originalData: List<Lineup> = emptyList()
    lateinit var filters: FilterState

    fun setNewLineup(context: Context, lineup: LineupRequestInteractor.LineupForToday) {
        originalData = listOfNotNull(
            lineup.lineupNow.first,
            lineup.lineupNow.second,
            lineup.lineupThen.first,
            lineup.lineupThen.second
        )
        val make = DataSetCreator(context).make(originalData, filters)
        updateDataSet(make, false)
    }

    fun setFilterState(context: Context, filters: FilterState) {
        this.filters = filters
        updateDataSet(DataSetCreator(context).make(originalData, filters), false)
    }
}

class DataSetCreator(val context: Context) {
    fun make(
        lineups: List<Lineup>,
        filters: FilterState
    ): List<ExpandableHeaderItem<VehicleItem>> {
        val data = mutableListOf<ExpandableHeaderItem<VehicleItem>>()

        lineups.associateBy({ it }) { lineup ->
            listOf(lineup.teamA.vehicles, lineup.teamB.vehicles)
                .flatten()
                .filter { it.isFavorite }
        }.entries
            .forEachIndexed { index, (lineup, favorites) ->
                if (matchForLater_NowFilter(index, filters, lineups))
                    fillFavorite(lineup, data, favorites, isFavoriteActiveNow(index, lineups))
            }


        lineups.forEachIndexed { index, lineup ->
            if (matchForLater_NowFilter(index, filters, lineups))
                fillSet(lineup, data, filters)
        }
        return data
    }

    private fun isFavoriteActiveNow(
        index: Int,
        lineups: List<Lineup>
    ) = index < 2 && lineups.size > 2

    private fun matchForLater_NowFilter(
        index: Int,
        filters: FilterState,
        lineups: List<Lineup>
    ) =
        index < 2 && filters.nowLineupShow || index > 1 && filters.laterLineupShow || lineups.size == 2

    private fun fillSet(
        lineup: Lineup,
        dataset: MutableList<ExpandableHeaderItem<VehicleItem>>,
        filters: FilterState
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

    private fun fillFavorite(
        lineup: Lineup,
        dataset: MutableList<ExpandableHeaderItem<VehicleItem>>,
        favorite: List<Vehicle>,
        nowActive: Boolean = false
    ) {
        if (favorite.isNotEmpty()) {
            val header = "${lineup.lineupEntity.name} ${context.getString(R.string.favorites)}"
            val headerItem =
                ExpandableHeaderItem<VehicleItem>(
                    header.hashCode(), header, HeaderColors.getRandom(), nowActive
                )
            dataset += headerItem
            headerItem.items.addAll(favorite.map {
                VehicleItem(
                    it, headerItem.title
                )
            })
        }
    }

    private fun fillTeam(
        team: Team,
        dataset: MutableList<ExpandableHeaderItem<VehicleItem>>,
        filters: FilterState,
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
                        val headerItem =
                            ExpandableHeaderItem<VehicleItem>(
                                header.hashCode(),
                                header,
                                HeaderColors.getByVehicleType(
                                    team.teamEntity.teamLetter == "A",
                                    type
                                )
                            )
                        dataset += headerItem
                        fillVehicleList(list, headerItem)
                    }
                }
            }
        }
    }


    private fun fillVehicleList(
        vehicleList: List<Vehicle>,
        headerItem: ExpandableHeaderItem<VehicleItem>
    ) {
        headerItem.items.addAll(
            vehicleList
                .sortedBy { it.isFavorite }
                .sortedBy { it.nation }
                .map { vehicle ->
                    VehicleItem(vehicle, headerItem.title)
                })
    }
}