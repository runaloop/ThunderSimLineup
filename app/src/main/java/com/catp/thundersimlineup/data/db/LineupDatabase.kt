package com.catp.thundersimlineup.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.catp.thundersimlineup.data.db.entity.*
import com.catp.thundersimlineup.data.db.entity.converter.InstantConverter
import com.catp.thundersimlineup.data.db.entity.converter.LineupTypeConverter
import com.catp.thundersimlineup.data.db.entity.converter.VehicleStatusConverter
import com.catp.thundersimlineup.data.db.entity.converter.VehicleTypeConverter

@Database(
    entities = [
        TeamWithVehicleCrossRef::class,
        Vehicle::class,
        TeamEntity::class,
        LineupEntity::class,
        DataVersion::class,
        LineupCycleEntity::class,
        LineupCycleAvailabilityEntity::class,
        LineupToBREntity::class,
        LineupShiftEntity::class
    ],
    version = 1
)
@TypeConverters(
    VehicleTypeConverter::class,
    VehicleStatusConverter::class,
    LineupTypeConverter::class,
    InstantConverter::class
)
abstract class LineupDatabase : RoomDatabase() {
    abstract fun getLineupDao(): LineupDao

    companion object {
        @Volatile
        private var INSTANCE: LineupDatabase? = null

        fun getInstance(context: Context): LineupDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                LineupDatabase::class.java, "Lineups.db"
            )
                .build()

    }
}