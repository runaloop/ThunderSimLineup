package com.catp.thundersimlineup.data.db.entity.converter

import androidx.room.TypeConverter
import com.catp.model.VehicleType

class VehicleTypeConverter {
    @TypeConverter
    fun toVehicleType(type: String): VehicleType {
        return VehicleType.valueOf(type)
    }

    @TypeConverter
    fun toString(vehicleType: VehicleType): String {
        return vehicleType.toString()
    }
}