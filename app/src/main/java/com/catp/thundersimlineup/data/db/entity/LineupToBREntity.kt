package com.catp.thundersimlineup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = LineupCycleEntity::class,
        parentColumns = ["id"],
        childColumns = ["lineupId"]
    )]
)
data class LineupToBREntity(
    val lineupId: Long,
    val supportedBR: Double,
    @PrimaryKey(autoGenerate = true) var id: Long = 0
)