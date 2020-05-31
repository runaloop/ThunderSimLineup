package com.catp.thundersimlineup.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class Lineup(
    @Embedded var lineupEntity: LineupEntity,
    @Relation(parentColumn = "teamAId", entityColumn = "teamId", entity = TeamEntity::class)
    val teamA: Team=Team(TeamEntity("", "A"), mutableListOf()),
    @Relation(parentColumn = "teamBId", entityColumn = "teamId", entity = TeamEntity::class)
     val teamB: Team=Team(TeamEntity("", "B"), mutableListOf())
)