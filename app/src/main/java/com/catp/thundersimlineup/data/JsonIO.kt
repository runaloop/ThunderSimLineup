package com.catp.thundersimlineup.data

import com.catp.model.JsonLineupConfig
import com.dslplatform.json.DslJson
import toothpick.InjectConstructor
import java.io.InputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject

@InjectConstructor
class JsonIO() {
    @Inject
    lateinit var dslJson: DslJson<JsonLineupConfig>
    fun readZip(inputStream: InputStream): JsonLineupConfig {
        ZipInputStream(inputStream).use { zipInputStream ->
            zipInputStream.nextEntry
            return read(zipInputStream)
        }
    }

    private fun read(inputStream: InputStream): JsonLineupConfig {
        inputStream.use { stream ->
            val lineupConfig = dslJson.deserialize(JsonLineupConfig::class.java, stream)
            return lineupConfig
        }
    }

    /*fun write(outputStream: OutputStream, jsonLineupConfig: JsonLineupConfig) {
        outputStream.use {
            JsonLib.get().serialize(jsonLineupConfig, it)
        }
    }*/
}