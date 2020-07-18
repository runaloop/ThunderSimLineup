package com.catp.thundersimlineup

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.catp.thundersimlineup.ui.lineuplist.HeaderPositionCalculatorWoErrorSpam
import com.catp.thundersimlineup.ui.lineuplist.StickyHeaderAdapter
import com.catp.thundersimlineup.ui.lineuplist.StickyRecyclerHeadersDecorationWithOpenConstructor
import com.catp.thundersimlineup.ui.vehiclelist.VehicleItem
import com.mikepenz.fastadapter.FastAdapter
import com.timehop.stickyheadersrecyclerview.caching.HeaderViewCache
import com.timehop.stickyheadersrecyclerview.calculation.DimensionCalculator
import com.timehop.stickyheadersrecyclerview.rendering.HeaderRenderer
import com.timehop.stickyheadersrecyclerview.util.LinearLayoutOrientationProvider

fun initRecyclerView(fastAdapter: FastAdapter<VehicleItem>, recyclerView: RecyclerView, stickyHeaderAdapter: StickyHeaderAdapter<VehicleItem>) {
    val orientationProvider = LinearLayoutOrientationProvider()
    val headerProvider = HeaderViewCache(stickyHeaderAdapter, orientationProvider)
    val dimensionCalculator = DimensionCalculator()

    val decoration = StickyRecyclerHeadersDecorationWithOpenConstructor(
        stickyHeaderAdapter,
        HeaderRenderer(orientationProvider),
        orientationProvider,
        dimensionCalculator,
        headerProvider,
        HeaderPositionCalculatorWoErrorSpam(
            stickyHeaderAdapter,
            headerProvider,
            orientationProvider,
            dimensionCalculator
        ),
        null
    )
    recyclerView.addItemDecoration(decoration)
    recyclerView.itemAnimator = DefaultItemAnimator()
    recyclerView.adapter = stickyHeaderAdapter.wrap(fastAdapter)
}
