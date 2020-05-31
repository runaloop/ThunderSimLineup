package com.catp.thundersimlineup.data

import com.catp.model.JsonLineupConfig
import toothpick.InjectConstructor
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipInputStream
@InjectConstructor
class JsonIO {
    fun readZip(inputStream: InputStream): JsonLineupConfig {
        ZipInputStream(inputStream).use { zipInputStream ->
            zipInputStream.nextEntry
            return read(zipInputStream)
        }
    }

    fun read(inputStream: InputStream): JsonLineupConfig {
        inputStream.use { stream ->
            println("LineupIO starts parsing")
            val dslJson = JsonLib.JSON()
            val lineupConfig = dslJson.deserialize(JsonLineupConfig::class.java, stream)
            println("LineupIO finished parsing")
            return lineupConfig
            throw error("Can't load local config")
        }
    }

    fun write(outputStream: OutputStream, jsonLineupConfig: JsonLineupConfig) {
        outputStream.use {
            JsonLib.JSON().serialize(jsonLineupConfig, it)
        }
    }
}