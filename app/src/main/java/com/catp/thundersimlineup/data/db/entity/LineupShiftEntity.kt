package com.catp.thundersimlineup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate

@Entity(
    foreignKeys = [ForeignKey(
        entity = LineupCycleEntity::class,
        parentColumns = ["id"],
        childColumns = ["lineupId"]
    )]
)
data class LineupShiftEntity(
    val lineupId: Long,
    val shiftDate: LocalDate, @PrimaryKey(autoGenerate = true) var id: Long = 0
)