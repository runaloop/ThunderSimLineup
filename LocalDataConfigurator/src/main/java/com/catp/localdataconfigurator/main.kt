package com.catp.localdataconfigurator

import com.github.ajalt.clikt.core.subcommands

@ExperimentalStdlibApi
fun main(args: Array<String>) = MainParser()
    .subcommands(ParseGameLineupData(), RegenerateXLSXFile(), GenerateJson(), ReadWTDump())
    .main(args)