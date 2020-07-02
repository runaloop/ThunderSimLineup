package com.catp.localdataconfigurator

import java.io.BufferedReader
import java.io.File

@ExperimentalStdlibApi
class WTDumpReader(val fileName: String) {
    lateinit var reader: BufferedReader
    var buffer: CharArray = CharArray(DEFAULT_BUFFER_SIZE)
    val jsonRegex = Regex(ITEM_JSON)
    val strings = mutableListOf<Pair<String, Int>>()
    var currentChunk = 0
    var indexOffset = 0 // Used if prev item was partial

    enum class FILE_PARSE_STATE {
        CHUNK_PARSED,
        NO_MORE_DATA,
        CHUNK_PARSED_LAST_ITEM_PARTIAL
    }

    enum class CHUNK_PARSE_STATE {
        ITEM_SKIPPED,
        ITEM_PARSED,
        ITEM_PARSED_PARTIALY,// returns if readNextItem can't read item cause of the end of chunk
    }
    //open file
    //read next chunk
    //find lineup - is it full?

    fun openFile(): BufferedReader {
        return File(fileName).bufferedReader()
    }

    fun parseFile() {
        reader = openFile()
        var readStatus = FILE_PARSE_STATE.CHUNK_PARSED
        while (readNextChunk()) {
            readStatus = parseNextChunk(readStatus)
            currentChunk++
        }
        if (strings.isNotEmpty()) {
            val lineups = splitLineups()
            guessLineups(lineups)
        }
    }

    fun splitLineups(): List<List<String>> {
        val result = mutableListOf<MutableList<String>>()
        lateinit var currentList: MutableList<String>
        var lastPosition = -1
        strings.forEach { (item, position) ->
            if (lastPosition == -1 || position - lastPosition > MAX_DISTANCE_BETWEEN_LINEUPS) {
                currentList = mutableListOf()
                result += currentList
            }
            currentList.add(item)
            lastPosition = position
        }
        return result
    }

    fun guessLineups(lineups: List<List<String>>) {
        println(strings.joinToString("\n"))
    }

    fun readNextChunk(): Boolean {
        val bytesCount = reader.read(buffer)
        if (bytesCount == -1 || bytesCount == 0)
            return false
        return true
    }


    fun parseNextChunk(
        lastChunkState: FILE_PARSE_STATE
    ): FILE_PARSE_STATE {

        val partial = if (lastChunkState == FILE_PARSE_STATE.CHUNK_PARSED_LAST_ITEM_PARTIAL) {
            val partialData = strings.removeLast()
            indexOffset = -partialData.first.length
            partialData.first
        } else {
            indexOffset = 0
            ""
        }
        val data = partial + String(buffer)

        if (hasStartItem(data)) {
            val items = getStartItemsIndexed(data)
            items.map { index ->
                when (parseNextItem(data, index)) {
                    CHUNK_PARSE_STATE.ITEM_PARSED_PARTIALY -> {
                        return FILE_PARSE_STATE.CHUNK_PARSED_LAST_ITEM_PARTIAL
                    }
                    CHUNK_PARSE_STATE.ITEM_PARSED -> {
                        println("🦄Found item: " + strings.last())
                    }
                    CHUNK_PARSE_STATE.ITEM_SKIPPED -> {

                    }
                }
            }
        }
        return FILE_PARSE_STATE.CHUNK_PARSED
    }

    fun readPartialItem(
        data: String,
        startAt: Int
    ): CHUNK_PARSE_STATE {
        addResultString(data, startAt)
        return CHUNK_PARSE_STATE.ITEM_PARSED_PARTIALY
    }

    private fun addResultString(
        data: String,
        startAt: Int,
        endAt: Int = -1
    ) {
        //TODO: Test to make sure index calculated correctly
        val str = if (endAt == -1) data.substring(startAt) else data.substring(startAt, endAt)
        strings += Pair(str, startAt + DEFAULT_BUFFER_SIZE * currentChunk + indexOffset)
    }


    fun parseNextItem(
        data: String,
        startAt: Int
    ): CHUNK_PARSE_STATE {
        val (parseState, _, jsonFinish, json) = extractVehicleJson(
            data,
            startAt
        )

        when (parseState) {
            CHUNK_PARSE_STATE.ITEM_SKIPPED -> return CHUNK_PARSE_STATE.ITEM_SKIPPED
            CHUNK_PARSE_STATE.ITEM_PARSED_PARTIALY -> return readPartialItem(data, startAt)
        }

        val jsonMatch = jsonRegex.find(json)
        return if (jsonMatch != null && jsonMatch.groupValues.size == 2) {
            addResultString(data, startAt, jsonFinish + 1)
            CHUNK_PARSE_STATE.ITEM_PARSED
        } else {
            reportPosition(startAt, "☠️Can't parse item $json")
            CHUNK_PARSE_STATE.ITEM_SKIPPED
        }
    }


    fun extractVehicleJson(
        data: String,
        startAt: Int
    ): VehicleJsonItem {
        val jsonFinish = data.indexOf("}", startAt)
        val nextItemStart = data.indexOf(ITEM_START, startAt+1)
        if (
            (jsonFinish != -1 && (jsonFinish - startAt > JSON_MAX_LENGTH || jsonFinish - startAt < JSON_MINIMUM_LENGTH)) ||
            (nextItemStart != -1 && nextItemStart < jsonFinish)
        ) {
            return VehicleJsonItem(CHUNK_PARSE_STATE.ITEM_SKIPPED, startAt, jsonFinish, "")
        }


        val json = if (jsonFinish == -1) "" else data.substring(
            startAt,
            jsonFinish + 1
        )
        return VehicleJsonItem(
            if (jsonFinish == -1) CHUNK_PARSE_STATE.ITEM_PARSED_PARTIALY else CHUNK_PARSE_STATE.ITEM_PARSED,
            startAt,
            jsonFinish,
            json
        )
    }


    fun getStartItemsIndexed(data: String): List<Int> {
        var index = 0
        val result = mutableListOf<Int>()
        do {
            index = data.indexOf(ITEM_START, index)
            if (index != -1) {
                result += index
                index++
            }
        } while (index != -1)

        return result
    }

    fun getStartItemCount(data: String): Int {
        return data.split(ITEM_START).count() - 1
    }

    private fun hasStartItem(data: String): Boolean {
        return -1 != data.indexOf(ITEM_START)
    }


    fun reportPosition(position: Int, text: String) {
        println("Report at position: ${position + DEFAULT_BUFFER_SIZE * currentChunk + indexOffset} $text")
    }


    data class VehicleJsonItem(
        val parseState: CHUNK_PARSE_STATE,
        val startAt: Int,
        val finishAt: Int,
        val result: String
    )

    companion object {
        val ITEM_START = "{"
        val ITEM_FINISH = ",\"needShopInfo\":true,\"ttype\":\"UNIT\"\\}"
        val ITEM_JSON = "\\{\"id\":\"([a-zA-Z_\\-0-9]+)\",\"needShopInfo\":true,\"ttype\":\"UNIT\"\\}"
        val JSON_MAX_LENGTH = 300
        val JSON_MINIMUM_LENGTH = 44// id with empty filed and needShopInfo ttype fields
        val NEXT_ITEM_MAX_DISTANCE = 10
        val ITEM_TITLE_MIN_LENGTH = 10
        val MAX_DISTANCE_BETWEEN_LINEUPS = 500
    }
}
