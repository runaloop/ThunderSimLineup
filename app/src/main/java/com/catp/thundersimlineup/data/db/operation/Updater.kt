package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonLineupConfig
import com.catp.thundersimlineup.data.db.LineupDatabase
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class Updater {
    @Inject
    lateinit var db: LineupDatabase

    @Inject
    lateinit var updateProcess: UpdateProcess

    fun process(json: JsonLineupConfig) {
        updateProcess.prepare(json)
        db.runInTransaction(updateProcess)
    }

}