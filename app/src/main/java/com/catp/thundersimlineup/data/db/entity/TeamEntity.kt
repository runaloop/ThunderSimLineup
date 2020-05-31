package com.catp.thundersimlineup.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TeamEntity(
    val lineupName: String,
    val teamLetter: String, @PrimaryKey(autoGenerate = true) var teamId: Long = 0
)