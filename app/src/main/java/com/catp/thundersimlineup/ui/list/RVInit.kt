package com.catp.thundersimlineup.ui.list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.catp.thundersimlineup.MainActivityViewModel
import com.catp.thundersimlineup.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.IFlexible


fun configureRecyclerView(
    adapter: FlexibleAdapter<*>,
    recyclerView: RecyclerView,
    fragment: Fragment,
    activityViewModel: MainActivityViewModel?
) {

    adapter.expandItemsAtStartUp()
        .setAutoCollapseOnExpand(false)
        .setAutoScrollOnExpand(true)
        .setNotifyMoveOfFilteredItems(true)
        .setAnimationOnForwardScrolling(true)
        .setAnimationOnReverseScrolling(true)


    recyclerView.layoutManager = SmoothScrollLinearLayoutManager(fragment.requireContext())
    recyclerView.adapter = adapter
    recyclerView.addItemDecoration(
        FlexibleItemDecoration(fragment.requireContext())
            .addItemViewType(R.layout.list_header)
            .withOffset(4)
    )


    activityViewModel?.let {
        adapter.addListener(FlexibleAdapter.OnItemClickListener { _, position ->
            val item: IFlexible<*>? = adapter.getItem(position)
            if (item != null && item is VehicleItem) {
                activityViewModel.onFavoriteChange(item.vehicle)
            }
            adapter.notifyItemChanged(position)
            true
        })

    }
    adapter.setStickyHeaders(true)
}