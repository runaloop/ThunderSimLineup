package com.catp.localdataconfigurator

import com.catp.model.*
import com.github.ajalt.clikt.output.TermUi
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFTableStyleInfo
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream


class SpreedSheetGenerator(
    val lineups: List<JsonLineup>,
    private val vehicleStore: JsonVehicleStore
) {
    private lateinit var workBook: XSSFWorkbook
    private lateinit var sheet: XSSFSheet
    private var rowIndex = 1

    companion object {
        const val SPREED_SHEET_FILENAME = "actual_lineups.xlsx"
        const val SPREED_SHEET_FILENAME_BACKUP = "actual_lineups backup.xlsx"

        val subHeaders = listOf(
            "ID",
            "Type",
            "Nation",
            "BR",
            "FullEnglishTitle"
        )

        enum class HEADER {
            ID,
            Type,
            Nation,
            BR,
            FullEnglishTitle
        }


    }

    fun removeUglySymbolsFromTitles() {
        //full list - [ , ", ', (, ), *, ,, -, ., /, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, :, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, ª, ä, è, é, ö, ü, К, М, С, Т, а, —, №, ⋠, ␗, ␙, ␠, ▀, ▂, ▃, ▄, ▅, ]
        val toDelete = Regex("[⋠␗␙␠▀▂▃▄▅\uF059]")
        vehicleStore.vehicleList.forEach { item ->
            if (item.locale?.title?.contains(toDelete) == true) {
                val old = item.locale!!.title
                val new = item.locale!!.title.replace(toDelete, "")
                item.locale!!.title = new
            }
        }
    }

    fun removeForbidenIds() {
        val forbiddenIdsEndings = listOf("_football", "us_amx_13_75", "yt_cup_2019", "us_amx_13_90", "yt_cup_2019")
        val forbiddenTypes = listOf(VehicleType.SHIP)
        vehicleStore.vehicleList.removeAll { vehicle ->
            vehicle.type in forbiddenTypes || forbiddenIdsEndings.any { vehicle.name.contains(it) }
        }
    }
    fun make() {
        println("Generating xlsx")
        removeForbidenIds()
        removeUglySymbolsFromTitles()
        createFile()
        createTopHeader()
        createSubHeader()
        writeLineupData()
        writeVehicleStore()
        finishVisualStyle()
        saveFile()
        TermUi.echo("Done")
    }

    private fun finishVisualStyle() {
        listOf(subHeaders, lineupTitles).flatten().forEachIndexed { index, s ->
            if (index !in 1..5)
                sheet.autoSizeColumn(index)
        }

        val reference = workBook.creationHelper.createAreaReference(
            CellReference(1, 0), CellReference(rowIndex, subHeaders.size + lineupTitles.size - 1)
        )
        val table = sheet.createTable(
            reference
        )

        table.name = "Test"
        table.displayName = "Test_Table"
        // For now, create the initial style in a low-level way
        table.ctTable.addNewTableStyleInfo()
        table.ctTable.tableStyleInfo.name = "TableStyleMedium23"
        // Style the table
        val style = table.style as XSSFTableStyleInfo
        style.name = "TableStyleMedium23"
        style.setFirstColumn(true)
        style.setLastColumn(false)
        style.isShowRowStripes = true
        style.isShowColumnStripes = false

        sheet.createFreezePane(1, 2)

    }

    private fun writeVehicleStore() {
        vehicleStore.vehicleList
            .toSet().minus(lineups.map { it.fullVehicleList }.flatten()).toTypedArray()
            .sortedWith(compareBy<JsonVehicle> { it.br.toDouble() }.thenBy { it.type }.thenBy { it.nation })
            .forEach { vehicle ->
                vehicleToCell(vehicle)
            }
    }

    /**
     * LINEUP_CYCLE_RULE_1	1_1 3_1 2_1
    LINEUP_CYCLE_RULE_2	8_2 10_2 8_2_2
    LINEUP_SHIFT_RULE_1	25.12.2019	4
    LINEUP_SHIFT_RULE_2	25.12.2019	4
    LINEUP_PLANE_TO_BR_RELATION
     */


    private fun writeLineupData() {
        val vehicleList = lineups.map { it.fullVehicleList }.flatten().distinct()
            .sortedWith(compareBy<JsonVehicle> { it.br.toDouble() }.thenBy { it.type }.thenBy { it.nation })

        vehicleList.forEach { vehicle ->
            val row = vehicleToCell(vehicle)

            lineupTitles.forEachIndexed { index, lineupTitle ->
                val lineup =
                    lineups.find { it.name == lineupTitle }
                        ?: error("Can't find $lineupTitle in $lineups")
                if (lineup.hasVehicle(vehicle)) {
                    val team = if (lineup.jsonTeamA.hasVehicle(vehicle)) TeamType.A else TeamType.B
                    row.createCell(HEADER.values().size + index).setCellValue(team.name)
                }
            }
        }
    }

    private fun vehicleToCell(
        vehicle: JsonVehicle
    ): XSSFRow {

        val row = sheet.createRow(++rowIndex)
        val (id, type, nation, br, locale) = vehicle
        row.createCell(HEADER.ID).also {
            it.setCellValue(vehicle.locale!!.id)
        }
        row.createCell(HEADER.Type).setCellValue(type.name)
        row.createCell(HEADER.Nation).setCellValue(nation)
        row.createCell(HEADER.BR).setCellValue(br)
        row.createCell(HEADER.FullEnglishTitle).setCellValue(locale!!.title)

        if(listOf(id, nation, br, locale.title).any { it.isEmpty() }){
            println("Vehicle has empty fields: $vehicle")
        }
        return row
    }


    private fun saveFile() {
        File(SPREED_SHEET_FILENAME).renameTo(File(SPREED_SHEET_FILENAME_BACKUP))
        workBook.write(FileOutputStream(SPREED_SHEET_FILENAME))
        workBook.close()
    }

    private fun createFile() {
        workBook = XSSFWorkbook()
        sheet = workBook.createSheet("Lineup")
    }

    private fun createSubHeader() {

        val row = sheet.createRow(1)
        subHeaders.forEachIndexed { index, title ->
            row.createCell(index).setCellValue(title)
        }
        lineupTitles.forEachIndexed { index, title ->
            row.createCell(index + subHeaders.size).setCellValue(title)
        }
    }

    private fun createTopHeader() {
        val colCountVehileTitle = 7
        val colCountLineupTitle = 11
        val titleStyle = workBook.createCellStyle()
        titleStyle.verticalAlignment = VerticalAlignment.CENTER
        titleStyle.alignment = HorizontalAlignment.CENTER

        val bigTitleRow = sheet.createRow(0)

        val vehicleTitle = bigTitleRow.createCell(0)
        vehicleTitle.setCellValue("Vehicles")
        vehicleTitle.cellStyle = titleStyle

        val lineupTitle = bigTitleRow.createCell(colCountVehileTitle + 1)
        lineupTitle.setCellValue("Lineups")
        lineupTitle.cellStyle = titleStyle

        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, colCountVehileTitle))
        sheet.addMergedRegion(
            CellRangeAddress(
                0,
                0,
                colCountVehileTitle + 1,
                colCountVehileTitle + colCountLineupTitle
            )
        )

    }

}

private fun Row.createCell(id: SpreedSheetGenerator.Companion.HEADER): Cell {
    return createCell(id.ordinal)
}
