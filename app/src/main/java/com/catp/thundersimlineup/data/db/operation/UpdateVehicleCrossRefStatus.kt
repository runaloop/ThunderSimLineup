package com.catp.thundersimlineup.data.db.operation

import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.VehicleStatus

//Works on every update data, and deletes vehicles that marked to delete, and drops new status from vehicles to regular
class UpdateVehicleCrossRefStatus(val dao: LineupDao) {
    fun process() {
        var currentXrefs = dao.getTeamWithVehicleCrossRef()

        val toDelete = currentXrefs.filter { it.status == VehicleStatus.DELETED }
        if (toDelete.isNotEmpty()) {
            dao.deleteVehicleCrossRef(toDelete)
            currentXrefs = currentXrefs.toSet().minus(toDelete).toList()
        }

        val dropNewStatus = currentXrefs.filter { it.status == VehicleStatus.NEW }
        if (dropNewStatus.isNotEmpty())
            dao.updateVehicleCrossRef(dropNewStatus.map {
                it.status = VehicleStatus.REGULAR
                it
            })
    }
}