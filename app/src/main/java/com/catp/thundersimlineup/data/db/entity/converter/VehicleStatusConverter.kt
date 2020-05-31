package com.catp.thundersimlineup.data.db.entity.converter

import androidx.room.TypeConverter
import com.catp.thundersimlineup.data.db.entity.VehicleStatus

class VehicleStatusConverter {
    @TypeConverter
    fun toVehicleStatus(type: String): VehicleStatus {
        return VehicleStatus.valueOf(type)
    }

    @TypeConverter
    fun toString(vehicleStatus: VehicleStatus): String {
        return vehicleStatus.toString()
    }
}