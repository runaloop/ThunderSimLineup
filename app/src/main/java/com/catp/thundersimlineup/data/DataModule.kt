package com.catp.thundersimlineup.data

import com.catp.model.JsonLineupConfig
import com.dslplatform.json.DslJson
import toothpick.config.Module
import toothpick.ktp.binding.bind

class DataModule : Module() {
    init {
        bind<DslJson<JsonLineupConfig>>().toProvider(JsonProvider::class).singleton()
        bind<JsonIO>().singleton()
        bind<Schedule>().singleton()
        bind<LineupStorage>().singleton()
        bind<NetLoader>().singleton()
        bind<RefreshIntervalChecker>().singleton()
        bind<Storage>().singleton()
    }
}