package com.catp.thundersimlineup.data.db

import android.content.Context
import com.catp.thundersimlineup.data.db.operation.*
import toothpick.config.Module
import toothpick.ktp.binding.bind

class DBModule(context: Context) : Module() {
    init {
        val db = LineupDatabase.getInstance(context)
        bind<LineupDatabase>().toInstance(db)
        bind<LineupDao>().toInstance(db.getLineupDao())
        bind<ChangeDao>().toInstance(db.getChangeDao())

        bind<Changeset>().singleton()
        bind<CheckAndUpdateBR>().singleton()
        bind<CheckAndUpdateTitle>().singleton()
        bind<FindNewVehicles>().singleton()
        bind<UpdateLineupCycle>().singleton()
        bind<UpdateTeams>().singleton()
        bind<UpdateVehicleCrossRef>().singleton()
        bind<UpdateVehicleCrossRefStatus>().singleton()
        bind<UpdateVehicleStore>().singleton()
        bind<DBPopulate>().singleton()
    }
}