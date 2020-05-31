package com.catp.localdataconfigurator

import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileWriter
import java.net.URL

open class Loader {

    open fun load(url: String, tryLocalFirst: Boolean): String {
        val localFile = File(File(URL(url).path).name)
        return if (tryLocalFirst && localFile.exists()) {
            read("file:./" + localFile.name, true)
        } else {
            read(url, false)
        }
    }

    private fun read(url: String, local: Boolean): String {
        println("Start loading: $url")
        BufferedInputStream(URL(url).openStream()).use { `in` ->
            ByteArrayOutputStream().use { stream ->
                val dataBuffer = ByteArray(1024)
                var bytesRead: Int
                while (`in`.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                    stream.write(dataBuffer, 0, bytesRead)
                }
                val result = stream.toString()
                if (!local)
                    FileWriter(File(URL(url).path).name).use { it.write(result) }
                println("Finish loading: $url")
                return result
            }
        }


    }
}