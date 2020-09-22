package com.catp.thundersimlineup.ui.lineuplist

import android.content.Context
import com.catp.thundersimlineup.data.FilterState
import com.catp.thundersimlineup.data.db.entity.Lineup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import javax.inject.Inject

class LineupAdapter :
    FlexibleAdapter<AbstractFlexibleItem<*>>(null, null, true) {

    @Inject
    lateinit var dataSetCreator: DataSetCreator

    private var originalData: List<Lineup> = emptyList()
    private lateinit var filters: FilterState

    fun setNewLineup(context: Context, lineup: LineupRequestInteractor.LineupForToday) {
        originalData = listOfNotNull(
            lineup.lineupNow.first,
            lineup.lineupNow.second,
            lineup.lineupThen.first,
            lineup.lineupThen.second
        )
        val make = dataSetCreator.make(originalData, filters)
        updateDataSet(make, false)
    }

    fun setFilterState(context: Context, filters: FilterState) {
        this.filters = filters
        updateDataSet(dataSetCreator.make(originalData, filters), false)
    }
}

