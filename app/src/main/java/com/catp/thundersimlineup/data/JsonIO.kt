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

    private fun read(inputStream: InputStream): JsonLineupConfig {
        inputStream.use { stream ->
            val dslJson = JsonLib.get()
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