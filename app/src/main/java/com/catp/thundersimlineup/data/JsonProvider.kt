package com.catp.thundersimlineup.data

import com.catp.model.JsonLineupConfig
import com.dslplatform.json.DslJson
import com.dslplatform.json.runtime.Settings
import toothpick.InjectConstructor
import javax.inject.Provider


@InjectConstructor
class JsonProvider : Provider<DslJson<JsonLineupConfig>> {
    override fun get(): DslJson<JsonLineupConfig> =
        DslJson(Settings.basicSetup<JsonLineupConfig>().includeServiceLoader().allowArrayFormat(true))
}