package com.catp.localdataconfigurator

import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) = MainParser()
    .subcommands(ParseGameLineupData(), RegenerateXLSXFile(), GenerateJson())
    .main(args)