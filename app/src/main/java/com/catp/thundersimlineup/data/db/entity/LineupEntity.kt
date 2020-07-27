package com.catp.thundersimlineup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = TeamEntity::class,
        parentColumns = ["teamId"],
        childColumns = ["teamAId"]
    ), ForeignKey(
        entity = TeamEntity::class,
        parentColumns = ["teamId"],
        childColumns = ["teamBId"]
    )],
    indices = [Index("teamAId"), Index("teamBId")]
)
data class LineupEntity(
    @PrimaryKey val name: String,
    val teamAId: Long = 0,
    val teamBId: Long = 0
)