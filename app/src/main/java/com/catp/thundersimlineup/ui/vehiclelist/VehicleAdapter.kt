package com.catp.thundersimlineup.ui.vehiclelist

import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.mikepenz.fastadapter.adapters.ItemAdapter

class VehicleAdapter : ItemAdapter<VehicleItem>() {

    fun setData(items: List<Vehicle>) {
        val sorted = items.sortedWith(compareBy({!it.isFavorite}, {it.nation}))
            .map {
            if (it.isFavorite)
                VehicleItem(it, "Favorite")
            else
                VehicleItem(it, "${it.nation}")
        }
        set(sorted)
    }
}