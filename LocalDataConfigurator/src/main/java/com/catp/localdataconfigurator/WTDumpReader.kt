package com.catp.localdataconfigurator

import com.github.ajalt.clikt.output.TermUi
import java.io.BufferedReader
import java.io.File

@ExperimentalStdlibApi
class WTDumpReader(private val fileName: String, private val verbose: Boolean) {
    lateinit var reader: BufferedReader
    private var bufferSize = DEFAULT_BUFFER_SIZE*1000
    var buffer: CharArray = CharArray(bufferSize)
    private val jsonRegex = Regex(ITEM_JSON)
    val strings = mutableListOf<Pair<String, Int>>()
    private var currentChunk = 0
    private var indexOffset = 0 // Used if prev item was partial
    private lateinit var file: File

    enum class FileParseState {
        CHUNK_PARSED,
        CHUNK_PARSED_LAST_ITEM_PARTIAL
    }

    enum class ChunkParseState {
        ITEM_SKIPPED,
        ITEM_PARSED,
        ITEM_PARSED_PARTIALY,// returns if readNextItem can't read item cause of the end of chunk
    }
    //open file
    //read next chunk
    //find lineup - is it full?

    private fun openFile(): BufferedReader {
        file = File(fileName)
        return file.bufferedReader()
    }

    fun parseFile() {
        reader = openFile()
        var readStatus = FileParseState.CHUNK_PARSED
        val chunks = file.length() / bufferSize
        while (readNextChunk()) {
            readStatus = parseNextChunk(readStatus)
            currentChunk++
            val progress = currentChunk*100/chunks.toFloat()
            TermUi.echo("Parsed: ${"%.2f".format(progress)}%\r", trailingNewline = false)
        }
        if(readStatus == FileParseState.CHUNK_PARSED_LAST_ITEM_PARTIAL)
            strings.removeLast()
        TermUi.echo("Parsing complete")
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
        return result.filter { it.size > 20 }//skip small lists
    }

    private fun guessLineups(lineups: List<List<String>>) {
        val list =
        if(lineups.size > 2){
            TermUi.echo("Found ${lineups.size} lists of a vehicles")
            lineups.filter {
                TermUi.echo("$it")
                "y" == TermUi.prompt("🙉Would you like to process the list above?")
            }
        }else
            lineups
        ThunderLineupTxtGuesser().parse(list)
    }

    fun readNextChunk(): Boolean {
        val bytesCount = reader.read(buffer)
        if (bytesCount == -1 || bytesCount == 0)
            return false
        return true
    }


    fun parseNextChunk(
        lastChunkState: FileParseState
    ): FileParseState {

        val partial = if (lastChunkState == FileParseState.CHUNK_PARSED_LAST_ITEM_PARTIAL) {
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
                    ChunkParseState.ITEM_PARSED_PARTIALY -> {
                        return FileParseState.CHUNK_PARSED_LAST_ITEM_PARTIAL
                    }
                    ChunkParseState.ITEM_PARSED -> {
                        if (verbose) {
                            TermUi.echo("🦄Found item: " + strings.last())
                        }
                    }
                    ChunkParseState.ITEM_SKIPPED -> {

                    }
                }
            }
        }
        return FileParseState.CHUNK_PARSED
    }

    fun readPartialItem(
        data: String,
        startAt: Int
    ): ChunkParseState {
        if(data.length - startAt > JSON_MAX_LENGTH)
            return ChunkParseState.ITEM_SKIPPED
        addResultString(data, startAt)
        return ChunkParseState.ITEM_PARSED_PARTIALY
    }

    private fun addResultString(
        data: String,
        startAt: Int,
        endAt: Int = -1
    ) {
        val str = if (endAt == -1) data.substring(startAt) else data.substring(startAt, endAt)
        strings += Pair(str, startAt + bufferSize * currentChunk + indexOffset)
    }


    fun parseNextItem(
        data: String,
        startAt: Int
    ): ChunkParseState {
        val (parseState, _, jsonFinish, json) = extractVehicleJson(
            data,
            startAt
        )

        when (parseState) {
            ChunkParseState.ITEM_SKIPPED -> return ChunkParseState.ITEM_SKIPPED
            ChunkParseState.ITEM_PARSED_PARTIALY -> return readPartialItem(data, startAt)
        }

        val jsonMatch = jsonRegex.find(json)
        return if (jsonMatch != null && jsonMatch.groupValues.size == 2) {
            addResultString(data, startAt, jsonFinish + 1)
            ChunkParseState.ITEM_PARSED
        } else {
            reportPosition(startAt, "☠️Can't parse item $json")
            ChunkParseState.ITEM_SKIPPED
        }
    }


    fun extractVehicleJson(
        data: String,
        startAt: Int
    ): VehicleJsonItem {
        val jsonFinish = data.indexOf("}", startAt)
        val nextItemStart = data.indexOf(ITEM_START, startAt + 1)
        if (
            (jsonFinish != -1 && (jsonFinish - startAt > JSON_MAX_LENGTH || jsonFinish - startAt < JSON_MINIMUM_LENGTH)) ||
            (nextItemStart != -1 && nextItemStart < jsonFinish)
        ) {
            return VehicleJsonItem(ChunkParseState.ITEM_SKIPPED, startAt, jsonFinish, "")
        }


        val json = if (jsonFinish == -1) "" else data.substring(
            startAt,
            jsonFinish + 1
        )
        return VehicleJsonItem(
            if (jsonFinish == -1) ChunkParseState.ITEM_PARSED_PARTIALY else ChunkParseState.ITEM_PARSED,
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


    private fun reportPosition(position: Int, text: String) {
        if (verbose) {
            TermUi.echo("Report at position: ${position + bufferSize * currentChunk + indexOffset} $text")
        }
    }


    data class VehicleJsonItem(
        val parseState: ChunkParseState,
        val startAt: Int,
        val finishAt: Int,
        val result: String
    )

    companion object {
        const val ITEM_START = "{"
        const val ITEM_FINISH = ",\"needShopInfo\":true,\"ttype\":\"UNIT\"\\}"
        const val ITEM_JSON =
            "\\{\"id\":\"([a-zA-Z_\\-0-9]+)\",\"needShopInfo\":true,\"ttype\":\"UNIT\"\\}"
        const val JSON_MAX_LENGTH = 300
        const val JSON_MINIMUM_LENGTH = 44// id with empty filed and needShopInfo ttype fields
        const val MAX_DISTANCE_BETWEEN_LINEUPS = 500
    }
}
