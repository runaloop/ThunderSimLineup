package com.catp.thundersimlineup.ui.vehiclelist

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class VehicleItem(val vehicle: Vehicle, val header: String? = null) :
    AbstractItem<VehicleItem.ViewHolder>() {
    override val layoutRes: Int
        get() = R.layout.vehicle_list_item
    override val type: Int
        @SuppressLint("ResourceType")
        get() = R.layout.vehicle_list_item


    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<VehicleItem>(view) {
        val nation = view.findViewById<TextView>(R.id.tvNation)
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val br = view.findViewById<TextView>(R.id.tvBR)
        val heartEmpty = view.findViewById<ImageView>(R.id.ivHeartEmpty)
        val heartFilled = view.findViewById<ImageView>(R.id.ivHeartFilled)
        override fun bindView(item: VehicleItem, payloads: List<Any>) {
            nation.text = item.vehicle.nation
            title.text = item.vehicle.title
            br.text = item.vehicle.br
            if (item.vehicle.isFavorite) {
                heartFilled.visibility = View.VISIBLE
            } else
                heartFilled.visibility = View.GONE
        }

        override fun unbindView(item: VehicleItem) {
            nation.text = null
            title.text = null
            br.text = null
        }


    }
}