package com.catp.thundersimlineup.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.catp.thundersimlineup.data.db.entity.*
import com.catp.thundersimlineup.data.db.entity.converter.DateAndTimeConverter
import com.catp.thundersimlineup.data.db.entity.converter.LineupTypeConverter
import com.catp.thundersimlineup.data.db.entity.converter.VehicleStatusConverter
import com.catp.thundersimlineup.data.db.entity.converter.VehicleTypeConverter
import com.catp.thundersimlineup.data.db.migration.Migration1to2

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
        LineupShiftEntity::class,
        Change::class
    ],
    version = 2
)
@TypeConverters(
    VehicleTypeConverter::class,
    VehicleStatusConverter::class,
    LineupTypeConverter::class,
    DateAndTimeConverter::class
)
abstract class LineupDatabase : RoomDatabase() {
    abstract fun getLineupDao(): LineupDao
    abstract fun getChangeDao(): ChangeDao

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
                .addMigrations(Migration1to2())
                .build()

    }
}