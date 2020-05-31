package com.catp.thundersimlineup.data

import com.catp.model.JsonLineupConfig
import toothpick.ktp.delegate.inject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

const val LINEUP_URL = "https://raw.githubusercontent.com/runaloop/ThunderSimLineup/master/lineup"
const val LINEUP_SHIFT_URL =
    "https://raw.githubusercontent.com/runaloop/ThunderSimLineup/master/lineup"

class NetLoader {
    val loader by inject<JsonIO>()

    fun getETag(): String? {
        with(URL(LINEUP_URL).openConnection() as HttpsURLConnection) {
            requestMethod = "HEAD"
            return getHeaderField("ETag")
        }
    }

    fun getData(): JsonLineupConfig {
        return loader.readZip(URL(LINEUP_URL).openStream())
    }
}