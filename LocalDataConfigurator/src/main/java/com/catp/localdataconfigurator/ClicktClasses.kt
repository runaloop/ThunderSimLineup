package com.catp.localdataconfigurator

import com.catp.model.JsonLineupConfig
import com.catp.model.JsonRules
import com.catp.model.vehicleStore
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class MainParser : CliktCommand() {
    override fun run() = Unit
}

/**
 * Get data from local xlsx, an wpcost+unitIDlocale(can be local, or from net), and regenerate xlsx with new data
 */
class RegenerateXLSXFile : CliktCommand() {
    val useLocalFiles: Boolean by option().flag(default = false)
    override fun run() {
        val unitIDLocale = UnitIDLocale().apply { loadData(useLocalFiles) }
        val wpCost = WPCost().apply { loadData(useLocalFiles) }
        val lineups = SpreedSheetReader(vehicleStore).read()
        LineupToLocaleMatcher(lineups, wpCost, unitIDLocale, vehicleStore).process()
        //Remove from _1 lineups all planes, and add it with BR value
        LineupMatchPlanesWithBR(lineups, vehicleStore).process()
        SpreedSheetGenerator(lineups, vehicleStore).make()
    }
}

class GenerateJson : CliktCommand() {
    override fun run() {
        val lineups = SpreedSheetReader(vehicleStore).read()
        val baos = ByteArrayOutputStream()
        val version = TermUi.prompt(
            "Enter json version(if you have any changes, and version would be equal to client version, no data update will happen)",
            default = JsonRules.VERSION.toString(),
            showDefault = true
        )!!
        json.serialize(
            JsonLineupConfig(lineups, vehicleStore, JsonRules(), version.toInt()),
            baos
        )
        ZipOutputStream(FileOutputStream(File("app/src/main/res/raw/actual_lineup.zip"))).use {
            it.putNextEntry(ZipEntry("actual_lineup.json"))
            it.write(baos.toByteArray())
        }
        TermUi.echo("Done")
    }

}


@ExperimentalStdlibApi
class ReadWTDump: CliktCommand(){
    val verbose by option().flag(default = false)
    val fileToParse by argument().default("LocalDataConfigurator/")

    override fun run() {
        with(File(fileToParse)){
            if(isDirectory) {
                listFiles().filter { it.length() > 1000000 }.forEach { file->
                    if("y" == TermUi.prompt("Would you like to parse file: $file"))
                        WTDumpReader(file.absolutePath, verbose).parseFile()
                }
            }else{
                WTDumpReader(fileToParse, verbose).parseFile()
            }
        }

    }
}