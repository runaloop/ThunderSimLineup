package com.catp.thundersimlineup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["teamId", "vehicleId"],
    indices = [Index("vehicleId")],
    foreignKeys = [ForeignKey(
        entity = TeamEntity::class,
        parentColumns = ["teamId"],
        childColumns = ["teamId"]
    ), ForeignKey(
        entity = Vehicle::class,
        parentColumns = ["vehicleId"],
        childColumns = ["vehicleId"]
    )]
)
data class TeamWithVehicleCrossRef(
    val teamId: Long,
    val vehicleId: String,
    var status: VehicleStatus = VehicleStatus.REGULAR
)

enum class VehicleStatus {
    NEW, REGULAR, DELETED
}