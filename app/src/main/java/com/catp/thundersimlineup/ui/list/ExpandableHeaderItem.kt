package com.catp.thundersimlineup.ui.list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.catp.thundersimlineup.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IExpandable
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHeader
import eu.davidea.viewholders.ExpandableViewHolder

class ExpandableHeaderItem(
    val id: Int,
    val title: String,
    val headerColor: Int
) :
    AbstractFlexibleItem<ExpandableHeaderItem.ExpandableHeaderViewHolder>(),
    IExpandable<ExpandableHeaderItem.ExpandableHeaderViewHolder, VehicleItem>,
    IHeader<ExpandableHeaderItem.ExpandableHeaderViewHolder> {
    var _expanded = true

    init {
        isDraggable = false
        isHidden = false
        isSelectable = false
    }

    val items = mutableListOf<VehicleItem>()

    class ExpandableHeaderViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ) :
        ExpandableViewHolder(view, adapter, true) {
        val textView = view.findViewById<TextView>(R.id.tvTitle)
        val imgCollapse = view.findViewById<ImageView>(R.id.ivCollapse)
        val background: View = view
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
        holder: ExpandableHeaderViewHolder,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        holder.textView.text = "$title(${items.size})"
        val degree = if (isExpanded && !subItems.isEmpty()) 0 else -90
        holder.imgCollapse.rotation = degree.toFloat()
        holder.background.setBackgroundColor(headerColor)
    }





    override fun getLayoutRes(): Int = R.layout.list_header

    override fun getExpansionLevel(): Int = 0

    override fun isExpanded(): Boolean {
        return _expanded
    }

    override fun setExpanded(expanded: Boolean) {
        _expanded = expanded
    }

    override fun getSubItems(): MutableList<VehicleItem> = items
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ExpandableHeaderItem) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
    ): ExpandableHeaderViewHolder {
        return ExpandableHeaderViewHolder(
            view,
            adapter
        )
    }


}