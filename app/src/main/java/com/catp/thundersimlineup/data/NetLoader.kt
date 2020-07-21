package com.catp.thundersimlineup.data

import com.catp.model.JsonLineupConfig
import toothpick.ktp.delegate.inject
import java.net.URL
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

const val LINEUP_URL = "https://github.com/runaloop/ThunderSimLineup/raw/master/app/src/main/res/raw/actual_lineup.zip"

class NetLoader {
    @Inject
    lateinit var loader:JsonIO

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