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
                jsonVehicle.locale!!.title,
                jsonVehicle.br,
                false
            )

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vehicle) return false

        if (vehicleId != other.vehicleId) return false

        return true
    }

    override fun hashCode(): Int {
        return vehicleId.hashCode()
    }

}