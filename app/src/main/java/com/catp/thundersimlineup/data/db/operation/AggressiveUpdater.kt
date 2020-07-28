package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonLineupConfig
import com.catp.thundersimlineup.data.db.LineupDatabase
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class AggressiveUpdater {
    @Inject
    lateinit var errorReporter: ErrorReporter

    @Inject
    lateinit var favorites: FavoriteSaver

    @Inject
    lateinit var updater: Updater

    @Inject
    lateinit var db: LineupDatabase


    fun process(
        json: JsonLineupConfig,
        e: Exception
    ) {

        errorReporter.process(
            e,
            "Trying aggressive update: jsonVersion: ${json.version}, db version: ${getDBVersion()}"
        )
        favorites.save()
        db.clearAllTables()
        try {
            updater.process(json)
        } catch (ae: Exception) {
            errorReporter.process(
                ae,
                "Aggressive update failed: jsonVersion: ${json.version}, db version: ${getDBVersion()}"
            )
            throw ae
        }
        favorites.restore()
    }

    private fun getDBVersion(): String {
        return try {
            val version = db.getLineupDao().getVersion()?.version
            version?.toString() ?: "-1"
        } catch (e: Exception) {
            "E-1"
        }

    }
}