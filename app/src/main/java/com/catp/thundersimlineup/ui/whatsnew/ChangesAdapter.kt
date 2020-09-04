package com.catp.thundersimlineup.ui.whatsnew

import com.catp.thundersimlineup.BuildConfig
import com.catp.thundersimlineup.data.db.entity.Change
import com.catp.thundersimlineup.ui.list.ExpandableHeaderItem
import com.catp.thundersimlineup.ui.list.HeaderColors
import com.catp.thundersimlineup.ui.list.LogItem

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem


class ChangesAdapter : FlexibleAdapter<AbstractFlexibleItem<*>>(null, null, false) {

    fun setData(items: List<Change>) {
        val s = items
            .groupBy { it.date }
            .map { (date, list) ->
                val header = ExpandableHeaderItem<LogItem>(
                    date.hashCode(),
                    date.toString(),
                    HeaderColors.getRandom()
                )
                header.items += list.map {
                    LogItem(it.text, header.title)
                }
                Pair(date, header)
            }
            .sortedByDescending { it.first }
            .map { it.second }
        if (BuildConfig.DEBUG) {
            s.first().items.forEach {
                println("ChangeAdapter: ${it.text}")
            }
        }
        updateDataSet(s, false)
    }

}
