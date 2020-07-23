package com.catp.thundersimlineup.ui.vehiclelist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.catp.thundersimlineup.ui.list.VehicleItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

class VehicleAdapter : FlexibleAdapter<AbstractFlexibleItem<FlexibleViewHolder>>(null) {

    fun setData(items: List<Vehicle>) {

//        val list = mutableListOf<AbstractFlexibleItem<FlexibleViewHolder>>(Expandable(), SubItem())
//        updateDataSet(list)

        val sorted = items.sortedWith(compareBy({ !it.isFavorite }, { it.nation }))
            .map {
                if (it.isFavorite)
                    VehicleItem(it)
                else
                    VehicleItem(it)
            }
    }
}

class SubItem : AbstractFlexibleItem<FlexibleViewHolder>() {
    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
        holder: FlexibleViewHolder?,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun createViewHolder(
        view: View?,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?
    ): FlexibleViewHolder {
        TODO("Not yet implemented")
    }

    override fun getLayoutRes(): Int {
        TODO("Not yet implemented")
    }

}

/*
class Expandable : AbstractExpandableHeaderItem<Expandable.EViewHolder, >() {

    class EViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>) :
        ExpandableViewHolder(view, adapter) {

    }


}*/
