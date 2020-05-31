package com.catp.thundersimlineup.data.db.entity.converter

import androidx.room.TypeConverter
import com.catp.thundersimlineup.data.db.entity.LineupType
import com.catp.thundersimlineup.data.db.entity.VehicleStatus

class LineupTypeConverter {
    @TypeConverter
    fun toVehicleStatus(type: String): LineupType {
        return LineupType.valueOf(type)
    }

    @TypeConverter
    fun toString(lineupType: LineupType): String {
        return lineupType.toString()
    }
}