package com.catp.thundersimlineup.data.db

import android.content.Context
import com.catp.model.JsonLineupConfig
import com.catp.thundersimlineup.data.Preferences
import com.catp.thundersimlineup.data.db.operation.*
import toothpick.InjectConstructor

@InjectConstructor
class DBPopulate(
    val changeset: Changeset,
    val updateVehicleStore: UpdateVehicleStore,
    val updateLineupsTeams: UpdateTeams,
    val updateVehicleCrossRef: UpdateVehicleCrossRef,
    val updateVehicleCrossRefStatus: UpdateVehicleCrossRefStatus,
    val updateLineupCycle: UpdateLineupCycle,
    val lineupDao: LineupDao,
    val preferences: Preferences
) {
    fun updateData(jsonLineupConfig: JsonLineupConfig, context: Context) {
        val db = LineupDatabase.getInstance(context)
        db.runInTransaction {
            with(jsonLineupConfig) {
                try {
                    updateVehicleStore.process(jsonVehicleStore)
                    updateLineupsTeams.process(jsonLineups)
                    updateVehicleCrossRefStatus.process()
                    updateVehicleCrossRef.process(jsonLineups)
                    updateLineupCycle.process(jsonRules)
                    lineupDao.setVersion(jsonLineupConfig.version)
                } catch (e: Exception) {
                    //TODO: Correct exception handling, report to a server, delete of local data(except favorite list), and try to repopulate data
                    e.printStackTrace()
                }

            }
        }
        if (preferences.logVehicleEvents)
            changeset.writeChanges()
    }
}