package com.catp.thundersimlineup.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.catp.model.JsonVehicle
import com.catp.model.VehicleType

@Entity
data class Vehicle(
    @PrimaryKey val vehicleId: String, val type: VehicleType,
    val nation: String,
    var title: String,
    var br: String,
    var isFavorite: Boolean
) {
    companion object {
        fun fromJson(jsonVehicle: JsonVehicle): Vehicle =
            Vehicle(
                jsonVehicle.name,
                jsonVehicle.type,
                jsonVehicle.nation,
                jsonVehicle.locale!!.fullEnglishTitle,
                jsonVehicle.br,
                false
            )

    }
}