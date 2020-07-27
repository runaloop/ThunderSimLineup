package com.catp.thundersimlineup.data.db

import android.content.Context
import com.catp.model.JsonLineupConfig
import com.catp.thundersimlineup.data.Preferences
import com.catp.thundersimlineup.data.db.operation.*
import toothpick.InjectConstructor

@InjectConstructor
class DBPopulate(
    private val changes: Changeset,
    private val updateVehicleStore: UpdateVehicleStore,
    private val updateLineupsTeams: UpdateTeams,
    private val updateVehicleCrossRef: UpdateVehicleCrossRef,
    private val updateVehicleCrossRefStatus: UpdateVehicleCrossRefStatus,
    private val updateLineupCycle: UpdateLineupCycle,
    private val lineupDao: LineupDao,
    private val preferences: Preferences
) {
    fun updateData(jsonLineupConfig: JsonLineupConfig, context: Context) {
        val db = LineupDatabase.getInstance(context)
        db.runInTransaction {
            try {
                with(jsonLineupConfig) {
                    updateVehicleStore.process(jsonVehicleStore)
                    updateLineupsTeams.process(jsonLineups)
                    updateVehicleCrossRefStatus.process()
                    updateVehicleCrossRef.process(jsonLineups)
                    updateLineupCycle.process(jsonRules)
                }
                lineupDao.setVersion(jsonLineupConfig.version)
            } catch (e: Exception) {
                throw e
            }
        }
        if (preferences.logVehicleEvents)
            changes.writeChanges()
    }
}