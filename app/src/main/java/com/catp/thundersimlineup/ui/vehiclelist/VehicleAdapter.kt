package com.catp.thundersimlineup.ui.vehiclelist

import android.content.Context
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.catp.thundersimlineup.ui.list.ExpandableHeaderItem
import com.catp.thundersimlineup.ui.list.HeaderColors
import com.catp.thundersimlineup.ui.list.VehicleItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem

class VehicleAdapter : FlexibleAdapter<AbstractFlexibleItem<*>>(null, null, true) {

    fun setData(context: Context, items: List<Vehicle>) {
        val favorites = items.filter { it.isFavorite }.sortedBy { it.nation }
        context
        val favHeader = ExpandableHeaderItem(
            0,
            context.getString(R.string.favorites),
            HeaderColors.getRandom()
        )
        favHeader.items += favorites.map { VehicleItem(it, favHeader.title) }

        val rest = items.groupBy { it.nation }.map { (nation, list) ->
            val header =
                ExpandableHeaderItem(nation.hashCode(), nation, HeaderColors.getCountry(nation))
            header.items += list.map { VehicleItem(it, header.title) }
            header
        }.toMutableList()
        rest.add(0, favHeader)

        updateDataSet(rest.toList(), true)
    }

}

