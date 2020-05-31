package com.catp.thundersimlineup.data.db.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation


data class Team(
    @Embedded val teamEntity: TeamEntity,
    @Relation(
        entity = Vehicle::class,
        parentColumn = "teamId",
        entityColumn = "vehicleId",
        associateBy = Junction(TeamWithVehicleCrossRef::class)
    )
    val vehicles: List<Vehicle>
)
