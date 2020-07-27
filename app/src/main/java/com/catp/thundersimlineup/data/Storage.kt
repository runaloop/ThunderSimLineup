package com.catp.thundersimlineup.data

import android.content.Context
import com.catp.model.JsonLineupConfig
import com.catp.thundersimlineup.R
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class Storage {

    @Inject
    lateinit var jsonReader: JsonIO

    fun loadFromRAW(context: Context): JsonLineupConfig {
        return jsonReader.readZip(context.resources.openRawResource(R.raw.actual_lineup))
    }
}