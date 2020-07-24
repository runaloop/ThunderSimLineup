package com.catp.thundersimlineup.ui.list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.data.db.entity.Vehicle
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

class LogItem(val text: String, val header: String) :
    AbstractFlexibleItem<LogItem.ViewHolder>(), IFilterable<String> {

    class ViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>) :
        FlexibleViewHolder(view, adapter) {
        val title = view.findViewById<TextView>(R.id.tvTitle)
        fun bindView(item: LogItem) {
            title.text = item.text
        }
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
        holder: ViewHolder,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        holder.bindView(this)
    }

    override fun getLayoutRes(): Int = R.layout.log_list_item


    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ): ViewHolder {
        return ViewHolder(
            view,
            adapter
        )
    }

    override fun filter(constraint: String): Boolean {
        return text.contains(constraint, true)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LogItem) return false

        if (text != other.text) return false
        if (header != other.header) return false

        return true
    }

    override fun hashCode(): Int {
        var result = header.hashCode()
        result = 31 * result + text.hashCode()
        return result
    }


}