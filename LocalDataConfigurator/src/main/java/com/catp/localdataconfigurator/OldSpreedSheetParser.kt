package com.catp.localdataconfigurator


import com.catp.model.COMMAND_B_MARKER
import com.catp.model.LineupConfigurator
import com.catp.model.JsonVehicleStore
import de.siegmar.fastcsv.reader.CsvReader
import java.io.StringReader
import java.text.ParseException
import kotlin.text.Typography.nbsp

class OldSpreedSheetParser {
    fun parse(data: String): List<LineupConfigurator> {
        val store = JsonVehicleStore(mutableListOf())
        CsvReader()
            .apply {
                //this.setFieldSeparator(';')
            }
            .parse(StringReader(data)).use {

                //Read lineup titles and create lineups
                val setups = it.nextRow().fields
                    .filter { title -> title.isNotEmpty() }
                    .fold(listOf<LineupConfigurator>()) { acc, title ->
                        acc + LineupConfigurator(
                            if (title.startsWith("\uFEFF")) {
                                title.substring(1)
                            } else title
                        ,store)
                    }

                //Skip command A marker
                if (it.nextRow() == null) {
                    throw ParseException(
                        "Trying to skip command A line, but it can't be found",
                        -1
                    )
                }

                //Read vehicle info
                do {
                    val titles = it.nextRow()?.fields
                    titles?.let { line ->
                        line
                            .toList()
                            .chunked(4)
                            .forEachIndexed { index, (nation, title, empty, br) ->
                                val trimmedTitle = title.trim().replace(nbsp, ' ')
                                isTitleValid(trimmedTitle, nation, br, index)
                                if (!applyCommand(nation, trimmedTitle, setups[index]))
                                    setups[index].addVehicle(trimmedTitle, nation, br)
                            }
                    }
                } while (titles != null)
                return setups
            }
    }

    private fun isTitleValid(
        title: String,
        nation: String,
        br: String,
        index: Int
    ) {
        if (title.isNotEmpty() && (nation.isEmpty() || br.isEmpty()))
            throw ParseException(
                "Nation or BR Can't be empty, setup index:$index, vehicle: $title",
                -1
            )
    }

    /**
     * Checks if input is a command, and switches to commad B or vehicle type if so
     * @return true if input is a command, false otherwise
     */
    fun applyCommand(
        nation: String,
        title: String,
        lineup: LineupConfigurator
    ): Boolean {
        if (nation.isNotEmpty()) {
            if (nation.startsWith(COMMAND_B_MARKER)) {
                lineup.switchTeam()
                return true
            } else if (title.isEmpty()) {
                return true// stat line vehicle or aircraft count
            }
        } else if (title.isEmpty()) {
            lineup.switchVehicleType()
            return true
        }

        return false
    }
}
