package com.catp.thundersimlineup.data

import android.content.Context
import com.catp.model.JsonLineupConfig
import com.catp.thundersimlineup.R
import toothpick.InjectConstructor
import toothpick.ktp.delegate.inject

@InjectConstructor
class Storage {

    val jsonReader by inject<JsonIO>()

    fun loadFromRAW(context: Context): JsonLineupConfig {
        val lineupConfig =
            jsonReader.readZip(context.resources.openRawResource(R.raw.actual_lineup))
        return lineupConfig
    }
}