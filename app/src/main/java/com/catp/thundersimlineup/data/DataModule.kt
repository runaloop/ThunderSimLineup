package com.catp.thundersimlineup.data

import toothpick.config.Module
import toothpick.ktp.binding.bind

class DataModule : Module() {
    init {
        bind<JsonIO>().singleton()
        bind<Schedule>().singleton()
        bind<LineupStorage>().singleton()
        bind<NetLoader>().singleton()
        bind<RefreshIntervalChecker>().singleton()
        bind<Storage>().singleton()
    }
}