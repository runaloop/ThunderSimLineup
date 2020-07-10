package com.catp.localdataconfigurator

import com.catp.localdataconfigurator.SpreedSheetGenerator.Companion.HEADER
import com.catp.model.*
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream

class SpreedSheetReader(val vehicleStore: JsonVehicleStore) {
    lateinit var workBook: XSSFWorkbook
    lateinit var sheet: XSSFSheet
    val lineupHeader = mutableListOf<String>()
    val lineups = mutableListOf<JsonLineup>()

    fun read(): List<JsonLineup> {
        openFile()
        readLineupTitles()
        readTableData()
        val list = vehicleStore.vehicleList.toSet()
        vehicleStore.vehicleList.clear()
        vehicleStore.vehicleList.addAll(list) // remove duplicates caused of additional fill first in localeMatcher then here

        closeFile()
        return lineups
    }

    private fun readTableData() {
        var rawIndex = 2
        do {
            val row = sheet.getRow(rawIndex) ?: break
            val id = row.getCell(HEADER.ID)?.stringCellValue ?: break
            parseVehicle(id, row)
            rawIndex++
        } while (id.isNotEmpty())
    }

    private fun parseVehicle(id: String, row: XSSFRow) {

        val type = row.getCell(HEADER.Type)?.stringCellValue!!
        val nation = row.getCell(HEADER.Nation)?.stringCellValue!!
        val br = row.getCell(HEADER.BR)?.stringCellValue!!
        val fullEnglishTitle = row.getCell(HEADER.FullEnglishTitle)?.stringCellValue!!

        val vehicle = JsonVehicle(
            id,
            VehicleType.valueOf(type),
            nation,
            br,
            JsonLocaleItem(
                id,
                fullEnglishTitle,
                nation
            )
        )

        vehicleStore.vehicleList.add(vehicle)

        lineups.forEachIndexed { index, lineup ->
            val column = index + HEADER.values().size
            val data = row.getCell(column)?.stringCellValue ?: return@forEachIndexed
            when (data) {
                "A" -> lineup.jsonTeamA.vehicleIdList.add(vehicle.name)
                "B" -> lineup.jsonTeamB.vehicleIdList.add(vehicle.name)
                else -> return@forEachIndexed
            }
        }


    }

    private fun processCommand(id: String, row: XSSFRow) {


    }

    private fun readLineupTitles() {
        val row = sheet.getRow(1)
        var column = HEADER.values().size
        do {
            val data = row.getCell(column)?.stringCellValue
            data?.let {
                if (it.isNotEmpty()) {
                    lineupHeader += data
                    val lineup = JsonLineup(data)
                    lineups += lineup
                }
            }
            column++
        } while (data != null && data.isNotEmpty())
    }

    private fun closeFile() {
        workBook.close()
    }

    private fun openFile() {
        workBook = XSSFWorkbook(FileInputStream(SpreedSheetGenerator.SPREED_SHEET_FILENAME))
        sheet = workBook.getSheetAt(0)
    }
}

private fun XSSFRow.getCell(id: HEADER): XSSFCell? {
    return getCell(id.ordinal)
}
