package com.catp.thundersimlineup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate

//Used only with experimental lineups, which are available for short period of time
@Entity(
    foreignKeys = [ForeignKey(
        entity = LineupCycleEntity::class,
        parentColumns = ["id"],
        childColumns = ["lineupId"]
    )]
)
data class LineupCycleAvailabilityEntity(
    val lineupId: Long,
    val startOfLineup: LocalDate,
    val endOfLineup: LocalDate,
    @PrimaryKey(autoGenerate = true) var id: Long = 0
)