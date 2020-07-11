package com.catp.localdataconfigurator

import com.dslplatform.json.DslJson
import com.dslplatform.json.runtime.Settings
import com.github.ajalt.clikt.core.subcommands
val json = DslJson(Settings.withRuntime<Any>().includeServiceLoader().allowArrayFormat(true))
@ExperimentalStdlibApi
fun main(args: Array<String>) = MainParser()
    .subcommands(RegenerateXLSXFile(), GenerateJson(), ReadWTDump())
    .main(args)