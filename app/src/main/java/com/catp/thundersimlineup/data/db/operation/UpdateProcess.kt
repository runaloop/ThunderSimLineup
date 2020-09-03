package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonLineupConfig
import com.catp.thundersimlineup.data.db.LineupDao
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class UpdateProcess : Runnable {
    lateinit var json: JsonLineupConfig

    @Inject
    lateinit var updateVehicleStore: UpdateVehicleStore

    @Inject
    lateinit var updateLineupsTeams: UpdateTeams

    @Inject
    lateinit var updateVehicleCrossRef: UpdateVehicleCrossRef

    @Inject
    lateinit var updateVehicleCrossRefStatus: UpdateVehicleCrossRefStatus

    @Inject
    lateinit var updateLineupCycle: UpdateLineupCycle

    @Inject
    lateinit var lineupDao: LineupDao

    fun prepare(json: JsonLineupConfig) {
        this.json = json
    }

    override fun run() {
        with(json) {
            updateVehicleStore.process(jsonVehicleStore)
            updateLineupsTeams.process(jsonLineups)
            updateVehicleCrossRefStatus.process()
            updateVehicleCrossRef.process(jsonLineups)
            updateLineupCycle.process(jsonRules)
            updateVehicleCrossRefStatus.process() // to remove "deleted" xrefs after update, cause no use of that feature so far
        }
        lineupDao.setVersion(json.version)
    }

}