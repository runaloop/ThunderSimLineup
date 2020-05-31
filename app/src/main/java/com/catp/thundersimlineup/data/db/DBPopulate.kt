package com.catp.thundersimlineup.data.db

import android.content.Context
import com.catp.model.JsonLineupConfig
import com.catp.thundersimlineup.data.db.operation.*

class DBPopulate(
    val changeset: Changeset,
    val updateVehicleStore: UpdateVehicleStore,
    val updateLineupsTeams: UpdateTeams,
    val updateVehicleCrossRef: UpdateVehicleCrossRef,
    val updateVehicleCrossRefStatus: UpdateVehicleCrossRefStatus,
    val updateLineupCycle: UpdateLineupCycle,
    val lineupDao: LineupDao
) {
    fun updateData(jsonLineupConfig: JsonLineupConfig, context: Context) {
        val db = LineupDatabase.getInstance(context)
        db.runInTransaction {
            with(jsonLineupConfig) {
                updateVehicleStore.process(jsonVehicleStore)
                updateLineupsTeams.process(jsonLineups)
                updateVehicleCrossRefStatus.process()
                updateVehicleCrossRef.process(jsonLineups)
                updateLineupCycle.process(jsonRules)
                lineupDao.setVersion(jsonLineupConfig.version)
            }
        }

    }
}