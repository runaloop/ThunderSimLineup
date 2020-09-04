package com.catp.localdataconfigurator

import com.catp.model.JsonLocaleItem
import de.siegmar.fastcsv.reader.CsvReader
import java.io.StringReader

class UnitIDLocale {
    val localeData = mutableMapOf<String, JsonLocaleItem>()

    fun extractNationField(wpCost: WPCost) {
        localeData.values.forEach { locale ->
            wpCost.vehicleItems[locale.id]?.let {
                locale.nation = it.country
            }
        }
    }

    fun loadData(tryLocalFirst: Boolean = true) {
        val data = Loader().load(UNITCSV_PATH, tryLocalFirst)
        println("Parsing unit id locale")
        with(CsvReader()) {
            this.setContainsHeader(true)
            this.setFieldSeparator(';')
            this.parse(StringReader(data))
                .use { parser ->
                    var row = parser.nextRow()
                    do {
                        row = parser.nextRow()
                        row?.let {
                            val id = it.getField(ID_COLUMN_NAME)

                            IGNORE_WORDS_LIST.forEach { ignoreWord ->
                                if (id.endsWith(ignoreWord))
                                    return@let
                            }

                            val localeItem = JsonLocaleItem(
                                id,
                                row.getField(ID_COLUMN_TITLE) ?: ""
                            )

                            localeData[localeItem.id] = localeItem
                        }


                    } while (row != null)
                }
        }
        removeDuplicates()
        println("Finished")
    }


    private fun removeDuplicates() {
        // Id's divides on _0 _1 _2 _shop, but it just same id, so need to take _shop + _0 as title and full title, and drop _1 and _2
        val fullLocaleData = mutableMapOf<String, JsonLocaleItem>()
        val endings = listOf("0", "shop")
        localeData.values.forEach { item ->

            endings.forEach { ending ->
                if (item.id.endsWith("_$ending")) {

                    val id = item.id.replace(Regex("_$ending\$"), "")
                    val localeItem = fullLocaleData[id]
                    if (localeItem == null) {
                        fullLocaleData[id] = if (ending == "shop") {
                            JsonLocaleItem(
                                id,
                                item.title
                            )
                        } else
                            JsonLocaleItem(
                                id,
                                ""
                            )
                    } else {
                            localeItem.title = item.title
                    }
                }
            }
        }

        localeData.clear()
        localeData.putAll(fullLocaleData)
    }


    companion object {

        const val ID_COLUMN_NAME = "<ID|readonly|noverify>"
        const val ID_COLUMN_TITLE = "<English>"
        const val UNITCSV_PATH =
            //"https://github.com/VitaliiAndreev/WarThunder_JsonFileChanges/blob/master/Files/lang.vromfs.bin_u/lang/units.csv?raw=true"
            "https://github.com/VitaliiAndreev/WarThunder_JsonFileChanges_DevClient/blob/master/Files/lang.vromfs.bin_u/lang/units.csv?raw=true"
        val IGNORE_WORDS_LIST = listOf(
            "_1", "_2",
            "_race_0", "_race_1", "_race_2", "_race_shop", "_race",
            "_for_tutorial_0", "_for_tutorial_1", "_for_tutorial_2", "_for_tutorial_shop", "_for_tutorial",
            "_football"
        )
    }
}


