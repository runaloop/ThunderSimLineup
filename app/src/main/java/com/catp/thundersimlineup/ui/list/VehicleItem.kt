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

class VehicleItem(val vehicle: Vehicle, val header: String) :
    AbstractFlexibleItem<VehicleItem.ViewHolder>(), IFilterable<String> {

    class ViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>) :
        FlexibleViewHolder(view, adapter) {
        val nation: TextView = view.findViewById(R.id.tvNation)
        val title: TextView = view.findViewById(R.id.tvTitle)
        val br: TextView = view.findViewById(R.id.tvBR)
        private val heartFilled: ImageView = view.findViewById(R.id.ivHeartFilled)
        fun bindView(item: VehicleItem) {
            nation.text = item.vehicle.nation
            title.text = item.vehicle.title
            br.text = item.vehicle.br
            if (item.vehicle.isFavorite) {
                heartFilled.visibility = View.VISIBLE
            } else
                heartFilled.visibility = View.GONE
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

    override fun getLayoutRes(): Int = R.layout.vehicle_list_item


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
        return vehicle.title.contains(constraint, true)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VehicleItem) return false

        if (vehicle != other.vehicle) return false
        if (header != other.header) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vehicle.hashCode()
        result = 31 * result + header.hashCode()
        return result
    }


}