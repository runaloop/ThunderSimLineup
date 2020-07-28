package com.catp.thundersimlineup.data.db

import com.catp.model.JsonLineupConfig
import com.catp.thundersimlineup.data.Preferences
import com.catp.thundersimlineup.data.db.operation.AggressiveUpdater
import com.catp.thundersimlineup.data.db.operation.Updater
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class DBPopulate {

    @Inject
    lateinit var updater: Updater

    @Inject
    lateinit var aggressiveUpdater: AggressiveUpdater

    @Inject
    lateinit var preferences: Preferences

    @Inject
    lateinit var changes: Changeset

    fun updateData(json: JsonLineupConfig) {
        try {
            updater.process(json)
        } catch (e: Exception) {
            changes.clear()
            aggressiveUpdater.process(json, e)
        }
        writeVehicleLog()
    }

    private fun writeVehicleLog() {
        if (preferences.logVehicleEvents)
            changes.writeChanges()
    }
}