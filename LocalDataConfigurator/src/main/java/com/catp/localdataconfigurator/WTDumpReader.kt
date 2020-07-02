package com.catp.localdataconfigurator

import java.io.BufferedReader
import java.io.File

@ExperimentalStdlibApi
class WTDumpReader(val fileName: String) {
    lateinit var reader: BufferedReader
    var buffer: CharArray = CharArray(DEFAULT_BUFFER_SIZE)
    val jsonRegex = Regex(ITEM_JSON)
    val vehicleIdRegex = Regex(ITEM_ID_REGEX)
    val strings = mutableListOf<Pair<String, Int>>()
    var currentChunk = 0

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
        while(readNextChunk()) {
            readStatus = parseNextChunk(readStatus)
            currentChunk++
        }
        if(strings.isNotEmpty()){
            val lineups = splitLineups()
            guessLineups(lineups)
        }
    }

    fun splitLineups(): List<List<String>>{
        val result = mutableListOf<MutableList<String>>()
        lateinit var currentList: MutableList<String>
        var lastPosition = -1
        strings.forEach { (item, position) ->
            if(lastPosition == -1 || position - lastPosition > MAX_DISTANCE_BETWEEN_LINEUPS){
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
            partialData.first
        } else ""
        val data = partial + String(buffer)

        if (hasStartItem(data)) {
            val items = getStartItemsIndexed(data)
            items.map { index ->
                when (parseNextItem(data, index)) {
                    CHUNK_PARSE_STATE.ITEM_PARSED_PARTIALY -> {
                        println("👁PARTIAL")
                        return FILE_PARSE_STATE.CHUNK_PARSED_LAST_ITEM_PARTIAL
                    }
                    CHUNK_PARSE_STATE.ITEM_PARSED -> {
                        println("🦄Found item: " + strings.last())
                    }
                    CHUNK_PARSE_STATE.ITEM_SKIPPED ->{

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
        strings += Pair(str, startAt + DEFAULT_BUFFER_SIZE * currentChunk)
    }


    fun parseNextItem(
        data: String,
        startAt: Int
    ): CHUNK_PARSE_STATE {
        val vehicleIdParse = parseVehicleId(data, startAt)
        when (vehicleIdParse.second) {
            CHUNK_PARSE_STATE.ITEM_SKIPPED -> return CHUNK_PARSE_STATE.ITEM_SKIPPED
            CHUNK_PARSE_STATE.ITEM_PARSED_PARTIALY -> return readPartialItem(data, startAt)
        }

        val (parseState, _, jsonFinish, json) = extractVehicleJson(
            data,
            startAt + vehicleIdParse.first.length + ITEM_FINISH.length + ITEM_START.length

        )
        when (parseState) {
            CHUNK_PARSE_STATE.ITEM_SKIPPED -> return CHUNK_PARSE_STATE.ITEM_SKIPPED
            CHUNK_PARSE_STATE.ITEM_PARSED_PARTIALY -> return readPartialItem(data, startAt)
        }

        val jsonMatch = jsonRegex.find(json)
        return if (jsonMatch != null && jsonMatch.groupValues.size == 2) {
            if (jsonMatch.groupValues[1] != vehicleIdParse.first)
                CHUNK_PARSE_STATE.ITEM_SKIPPED
            else {
                addResultString(data, startAt, jsonFinish + 1)
                CHUNK_PARSE_STATE.ITEM_PARSED
            }
        } else {
            reportPosition(startAt, "☠️Can't parse item $json")
            CHUNK_PARSE_STATE.ITEM_SKIPPED
        }
    }


    fun extractVehicleJson(
        data: String,
        vehicleIdEndPosition: Int
    ): VehicleJsonItem {
        val jsonStart = data.indexOf("{", vehicleIdEndPosition)
        val jsonFinish = data.indexOf("}", jsonStart)
        val nextItemStart = data.indexOf(ITEM_START, vehicleIdEndPosition)
        if ((jsonStart == -1) ||
            (jsonStart != -1 && jsonStart - vehicleIdEndPosition > JSON_MAX_DISTANCE) ||
            (jsonStart != -1 && jsonFinish != -1 && (jsonFinish - jsonStart > JSON_MAX_LENGTH || jsonFinish - jsonStart < JSON_MINIMUM_LENGTH)) ||
            (nextItemStart != -1 && nextItemStart < jsonFinish)
        ) {
            return VehicleJsonItem(CHUNK_PARSE_STATE.ITEM_SKIPPED, jsonStart, jsonFinish, "")
        }


        val json = if (jsonFinish == -1 || jsonStart == -1) "" else data.substring(
            jsonStart,
            jsonFinish + 1
        )
        return VehicleJsonItem(
            if (jsonFinish == -1) CHUNK_PARSE_STATE.ITEM_PARSED_PARTIALY else CHUNK_PARSE_STATE.ITEM_PARSED,
            jsonStart,
            jsonFinish,
            json
        )
    }


    //[GARBAGE]#ui/gam[END OF CHUNK] should return index of first letter of a start marker
    fun doesChunkContainPartOfStartMarkerAtTheEnd(data: String): Int {
        if (
            data.length < ITEM_START.length ||
            data.substring(data.length - ITEM_START.length) == ITEM_START
        )
            return -1
        ITEM_START.length.downTo(1).forEach { n ->
            val index = data.length - n
            val part = data.substring(index)
            if (ITEM_START.startsWith(part))
                return index
        }
        return -1
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
        val last = doesChunkContainPartOfStartMarkerAtTheEnd(data)
        if (last != -1)
            result += last
        return result
    }

    fun getStartItemCount(data: String): Int {
        return data.split(ITEM_START).count() - 1 + (if(doesChunkContainPartOfStartMarkerAtTheEnd(data)!= -1) 1 else 0)
    }

    private fun hasStartItem(data: String): Boolean {
        return -1 != data.indexOf(ITEM_START) || doesChunkContainPartOfStartMarkerAtTheEnd(data) != -1
    }


    fun reportPosition(position: Int, text: String) {
        println("Report at position: ${position + DEFAULT_BUFFER_SIZE * currentChunk} $text")
    }

    fun parseVehicleId(data: String, startAt: Int): Pair<String, CHUNK_PARSE_STATE> {
        val endAt = data.indexOf(ITEM_FINISH, startAt)
        if (endAt == -1) {
            if (data.length - startAt < ITEM_TITLE_MAXIMUM_LENGTH)
                return Pair(
                    data.substring(startAt),
                    CHUNK_PARSE_STATE.ITEM_PARSED_PARTIALY
                )
        } else {
            val nextItem = data.indexOf(ITEM_START, startAt + 1)
            if (nextItem != -1 && endAt > nextItem) // {ITEM_START}garbage{ITEM_START}vehicle_id{ITEM_FINISH}
                return Pair("", CHUNK_PARSE_STATE.ITEM_SKIPPED)
            if (endAt - startAt > ITEM_TITLE_MAXIMUM_LENGTH) //{ITEM_START}long garbage{ITEM_FINISH}
                return Pair("", CHUNK_PARSE_STATE.ITEM_SKIPPED)
            val vehicleId = data.substring(startAt + ITEM_START.length, endAt)
            val match = vehicleIdRegex.matchEntire(vehicleId)
            return if (null != match) {
                Pair(vehicleId, CHUNK_PARSE_STATE.ITEM_PARSED)
            } else {
                reportPosition(
                    startAt,
                    "💂‍Can't match vehicle id with item id pattern '$vehicleId'"
                )
                Pair("", CHUNK_PARSE_STATE.ITEM_SKIPPED)
            }
            return Pair(vehicleId, CHUNK_PARSE_STATE.ITEM_PARSED)
        }
        return Pair("", CHUNK_PARSE_STATE.ITEM_SKIPPED)
    }

    data class VehicleJsonItem(
        val parseState: CHUNK_PARSE_STATE,
        val startAt: Int,
        val finishAt: Int,
        val result: String
    )

    companion object {
        val ITEM_ID_REGEX = "([a-z0-9_]+)"
        val ITEM_START = "#ui/gameuiskin#"
        val ITEM_FINISH = "_ico"
        val ITEM_JSON = "\\{\"id\":\"([a-zA-Z_0-9]+)\",\"needShopInfo\":true,\"ttype\":\"UNIT\"\\}"
        val JSON_MAX_DISTANCE = 10
        val JSON_MAX_LENGTH = 300
        val ITEM_TITLE_MAXIMUM_LENGTH = 255
        val NEXT_ITEM_MAX_DISTANCE = 10
        val ITEM_TITLE_MIN_LENGTH = 10
        val JSON_MINIMUM_LENGTH = 44// id with empty filed and needShopInfo ttype fields
        val MAX_DISTANCE_BETWEEN_LINEUPS = 500
    }
}
