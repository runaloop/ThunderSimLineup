package com.catp.model

import com.dslplatform.json.CompiledJson

@CompiledJson(formats = [CompiledJson.Format.ARRAY])
data class JsonLineupConfig(
    var jsonLineups: List<JsonLineup>,
    val jsonVehicleStore: JsonVehicleStore,
    val jsonRules: JsonRules,
    val version: Int = 0
)