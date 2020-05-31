package com.catp.thundersimlineup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = LineupEntity::class,
        parentColumns = ["name"],
        childColumns = ["lineupName"]
    )]
)
data class LineupCycleEntity(
    val lineupName: String,
    val type: LineupType,
    val orderNumber: Int,
    var planesByBR: Boolean = true, @PrimaryKey var id: Long = 0
)

enum class LineupType {
    LOW,
    TOP,
    EXCREMENTAL
}