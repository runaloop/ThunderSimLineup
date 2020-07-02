package com.catp.localdataconfigurator;


import com.catp.localdataconfigurator.WTDumpReader.CHUNK_PARSE_STATE.*
import com.catp.localdataconfigurator.WTDumpReader.Companion.ITEM_FINISH
import com.catp.localdataconfigurator.WTDumpReader.Companion.ITEM_START
import com.catp.localdataconfigurator.WTDumpReader.Companion.MAX_DISTANCE_BETWEEN_LINEUPS
import com.catp.localdataconfigurator.WTDumpReader.FILE_PARSE_STATE.CHUNK_PARSED
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.File

@ExperimentalStdlibApi
class WTDumpReaderTest {

    //@MockKForToothpick

    @RelaxedMockK
    lateinit var file: File

    @RelaxedMockK
    lateinit var reader: BufferedReader


    @InjectMockKs
    lateinit var dumpReader: WTDumpReader

    val itemVehicleId = "ru_bmp_1"
    val itemJsonValid = "{\"id\":\"$itemVehicleId\",\"needShopInfo\":true,\"ttype\":\"UNIT\"}"
    val itemJsonNotValid =
        "\\{{{{\"id\":\"$itemVehicleId\",\"needShopInfo\":true,\"ttype\":\"UNIT\"\\}"

    val listItem = ITEM_START + itemVehicleId + ITEM_FINISH + itemJsonValid

    @BeforeEach
    fun setUp() {
        dumpReader = WTDumpReader("fakefile")
        MockKAnnotations.init(this)
    }

    @Test
    fun injectedCorrectly() {
        assertThat(dumpReader).isNotNull()
        assertThat(reader).isEqualTo(dumpReader.reader)
    }

    @Test
    internal fun `reading chunk with EOF, should return no more data`() {
        //GIVEN
        every { reader.read(any<CharArray>()) }.returns(-1)
        //WHEN

        val result = dumpReader.readNextChunk()
        //THEN
        assertThat(result).isFalse()
    }

    @Test
    internal fun `reading chunk with zero length, should return no more data`() {
        //GIVEN
        every { reader.read(any<CharArray>()) }.returns(0)
        //WHEN
        val result = dumpReader.readNextChunk()
        //THEN
        assertThat(result).isFalse()
    }

    @Test
    internal fun `reading chunk with not zero length, should return true`() {
        //GIVEN
        every { reader.read(any<CharArray>()) }.returns(10)
        //WHEN
        val result = dumpReader.readNextChunk()
        //THEN
        assertThat(result).isTrue()
    }

    @Test
    internal fun `small chunk with no lineups, should return CHUNK_PARSED`() {
        //GIVEN
        dumpReader.buffer = "adfasdfasdfsadf".toCharArray()

        //WHEN
        val result = dumpReader.parseNextChunk(CHUNK_PARSED)

        //THEN
        assertThat(result).isEqualTo(CHUNK_PARSED)
    }

    @Test
    internal fun `getStartItemCount returns 0 if the string is empty`() {
        //GIVEN
        //WHEN
        val result = dumpReader.getStartItemCount("")
        //THEN
        assertThat(result).isEqualTo(0)
    }

    @Test
    internal fun `getStartItemCount returns 0 if the string does not have start items`() {
        //GIVEN
        //WHEN
        val result = dumpReader.getStartItemCount("ASDSAIKDASODUPQWDKSAJBDJHAS")
        //THEN
        assertThat(result).isEqualTo(0)
    }

    @Test
    internal fun `getStartItemCount returns 1 if the string has one item`() {
        //GIVEN
        //WHEN
        val result = dumpReader.getStartItemCount("AOSDJOSNDQOWD" + WTDumpReader.ITEM_START)
        //THEN
        assertThat(result).isEqualTo(1)
    }

    @Test
    internal fun `getStartItemCount returns 2 if the string has two items`() {
        //GIVEN
        //WHEN
        val result =
            dumpReader.getStartItemCount(WTDumpReader.ITEM_START + "AOSDJOSNDQOWD" + WTDumpReader.ITEM_START)
        //THEN
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `getItemsIndexed on empty strings should return empty list`() {
        //GIVEN
        //WHEN
        val result = dumpReader.getStartItemsIndexed("")
        //THEN
        assertThat(result).isEmpty()
    }

    @Test
    fun `getItemsIndexed on string with data, without start markers should return empty list`() {
        //GIVEN
        //WHEN
        val result = dumpReader.getStartItemsIndexed("asdfadfaf23rfsdfasf")
        //THEN
        assertThat(result).isEmpty()
    }

    @Test
    fun `getItemsIndexed on string with data should return list with indexed starts`() {
        //GIVEN
        //WHEN
        val result = dumpReader.getStartItemsIndexed(ITEM_START + ITEM_START + ITEM_START)
        //THEN
        assertThat(result).hasSize(3)
        assertThat(result).containsExactly(0, ITEM_START.length, ITEM_START.length * 2)
    }

    @Test
    fun `extractVehicleJson where no start, should return item skipped`() {
        //GIVEN
        //WHEN
        val result = dumpReader.extractVehicleJson("asdfasdfasdfasdfadf", 1)
        //THEN
        assertThat(result.parseState).isEqualTo(ITEM_SKIPPED)
        assertThat(result.startAt).isEqualTo(-1)
        assertThat(result.finishAt).isEqualTo(-1)
        assertThat(result.result).isEqualTo("")
    }


    @Test
    fun `parseVehicleId {ITEM_START}{END_OF_CHUNK} - check the length of a item for max length, and return skipped`() {
        //GIVEN
        val data = ITEM_START + String(CharArray(8000))
        //WHEN
        val result = dumpReader.parseVehicleId(data, 0)
        //THEN
        assertThat(result.first).isEqualTo("")
        assertThat(result.second).isEqualTo(ITEM_SKIPPED)
    }

    @Test
    fun `parseVehicleId {ITEM_START}{END_OF_CHUNK} - possible partial vehicle id return partial vehicle`() {
        //GIVEN
        val data = ITEM_START + "asdas"
        //WHEN
        val result = dumpReader.parseVehicleId(data, 0)
        //THEN
        assertThat(result.first).isEqualTo(data)
        assertThat(result.second).isEqualTo(ITEM_PARSED_PARTIALY)
    }


    @Test
    fun `parseVehicleId {ITEM_START}vehicle_id{END_OF_CHUNK} - partial vehicle id`() {
        //GIVEN
        val vehicleId = "ru_vehicle_id324"
        val data = ITEM_START + vehicleId
        //WHEN
        val result = dumpReader.parseVehicleId(data, 0)
        //THEN
        assertThat(result.first).isEqualTo(data)
        assertThat(result.second).isEqualTo(ITEM_PARSED_PARTIALY)
    }

    @Test
    fun `parseVehicleId {ITEM_START}vehicle_id{ITEM_FINISH} - full vehicle id`() {
        //GIVEN
        val vehicleId = "ru_vehicle_id324"
        val data = ITEM_START + vehicleId + ITEM_FINISH
        //WHEN
        val result = dumpReader.parseVehicleId(data, 0)
        //THEN
        assertThat(result.first).isEqualTo(vehicleId)
        assertThat(result.second).isEqualTo(ITEM_PARSED)
    }

    @Test
    fun `parseVehicleId {ITEM_START}vehicle_id{garbage}vehicle_id{ITEM_FINISH} - skip item`() {
        //GIVEN
        val vehicleId = "ru_vehicle_id324\u0000ru_vehicle_id323"
        val data = ITEM_START + vehicleId + ITEM_FINISH
        //WHEN
        val result = dumpReader.parseVehicleId(data, 0)
        //THEN
        assertThat(result.first).isEqualTo("")
        assertThat(result.second).isEqualTo(ITEM_SKIPPED)
    }

    @Test
    fun `parseVehicleId {ITEM_START}garbage{ITEM_FINISH} - check by regex, than skip`() {
        //GIVEN
        val vehicleId = "\u0000"
        val data = ITEM_START + vehicleId + ITEM_FINISH
        //WHEN
        val result = dumpReader.parseVehicleId(data, 0)
        //THEN
        assertThat(result.first).isEqualTo("")
        assertThat(result.second).isEqualTo(ITEM_SKIPPED)
    }

    @Test
    fun `parseVehicleId {ITEM_START}garbage{ITEM_FINISH} - check by the distance, than skip`() {
        //GIVEN
        val vehicleId = CharArray(2000) { '\u0000' }
        val data = ITEM_START + String(vehicleId) + ITEM_FINISH
        //WHEN
        val result = dumpReader.parseVehicleId(data, 0)
        //THEN
        assertThat(result.first).isEqualTo("")
        assertThat(result.second).isEqualTo(ITEM_SKIPPED)
    }

    @Test
    fun `parseVehicleId {ITEM_START}garbage{ITEM_START}vehicle_id{ITEM_FINISH} - check that there is a new ITEM_START before ITEM_FINISH, than skip`() {
        //GIVEN
        val vehicleId = CharArray(3) { 'a' }
        val realVehicleId = "ru_asd1"
        val data = ITEM_START + String(vehicleId) + ITEM_START + realVehicleId + ITEM_FINISH
        //WHEN
        val result = dumpReader.parseVehicleId(data, 0)
        //THEN
        assertThat(result.first).isEqualTo("")
        assertThat(result.second).isEqualTo(ITEM_SKIPPED)
    }

    @Test
    fun `parseVehicleId {ITEM_START}{ITEM_FINISH} - empty just skip`() {
        //GIVEN
        val vehicleId = ""
        val data = ITEM_START + vehicleId + ITEM_FINISH
        //WHEN
        val result = dumpReader.parseVehicleId(data, 0)
        //THEN
        assertThat(result.first).isEqualTo("")
        assertThat(result.second).isEqualTo(ITEM_SKIPPED)
    }

    @Test
    fun `extractVehicleJson {vehicleId}{garbage}{vehicleJson} - if distance is too much skip item, and json`() {
        //GIVEN
        val garbage = CharArray(2000) { 'a' }
        val data = String(garbage) + itemJsonValid
        //WHEN
        val result = dumpReader.extractVehicleJson(data, 0)
        //THEN
        assertThat(result.parseState).isEqualTo(ITEM_SKIPPED)
        assertThat(result.startAt).isEqualTo(2000)
        assertThat(result.finishAt).isEqualTo(2051)
        assertThat(result.result).isEqualTo("")
    }

    @Test
    fun `extractVehicleJson {vehicleId}{garbage}{vehicleJson} - if distnace is ok, return everything`() {
        //GIVEN
        val garbage = CharArray(9) { 'a' }
        val data = String(garbage) + itemJsonValid
        //WHEN
        val result = dumpReader.extractVehicleJson(data, 0)
        //THEN
        assertThat(result.parseState).isEqualTo(ITEM_PARSED)
        assertThat(result.startAt).isEqualTo(garbage.size)
        assertThat(result.finishAt).isEqualTo(data.length - 1)
        assertThat(result.result).isEqualTo(itemJsonValid)
    }

    @Test
    fun `{vehicleId}{START_ITEM}{vehicleJson} -if start item between json and vehicle id - skip`() {
        //GIVEN

        val data = ITEM_START + itemJsonValid
        //WHEN
        val result = dumpReader.extractVehicleJson(data, 0)
        //THEN
        assertThat(result.parseState).isEqualTo(ITEM_SKIPPED)
        assertThat(result.startAt).isEqualTo(15)
        assertThat(result.finishAt).isEqualTo(66)
        assertThat(result.result).isEqualTo("")
    }

    @Test
    fun `{vehicleId}{vehicleJsonStart}{END_OF_CHUNK} - check for distance and skip if more than needed`() {
        //GIVEN

        val data = itemJsonValid.substring(0, itemJsonValid.length / 2) +
                String(CharArray(300)) +
                itemJsonValid.substring(itemJsonValid.length / 2)
        //WHEN
        val result = dumpReader.extractVehicleJson(data, 0)
        //THEN
        assertThat(result.parseState).isEqualTo(ITEM_SKIPPED)
        assertThat(result.startAt).isEqualTo(0)
        assertThat(result.finishAt).isEqualTo(351)
        assertThat(result.result).isEqualTo("")
    }

    @Test
    fun `{vehicleId}{vehicleJsonStart}{END_OF_CHUNK} - check for distance and apply partial if its ok`() {
        //GIVEN

        val data = itemJsonValid.substring(0, itemJsonValid.length / 2)
        //WHEN
        val result = dumpReader.extractVehicleJson(data, 0)
        //THEN
        assertThat(result.parseState).isEqualTo(ITEM_PARSED_PARTIALY)
        assertThat(result.startAt).isEqualTo(0)
        assertThat(result.finishAt).isEqualTo(-1)
        assertThat(result.result).isEqualTo("")
    }

    @Test
    fun `extractVehicleJson very short json should skip item`() {
        //GIVEN

        val data = "{     }"
        //WHEN
        val result = dumpReader.extractVehicleJson(data, 0)
        //THEN
        assertThat(result.parseState).isEqualTo(ITEM_SKIPPED)
        assertThat(result.startAt).isEqualTo(0)
        assertThat(result.finishAt).isEqualTo(6)
        assertThat(result.result).isEqualTo("")
    }

    @Test
    fun `parseNextItem{ITEM_START}{garbage}{END_OF_CHUNK} - ITEM_SKIPPED as result`() {
        //GIVEN
        val data = ITEM_START + String(CharArray(2000))
        //WHEN
        val result = dumpReader.parseNextItem(data, 0)
        //THEN
        assertThat(result).isEqualTo(ITEM_SKIPPED)
    }

    @Test
    fun `parseNextItem{ITEM_START}{END_OF_CHUNK} - ITEM_PARSED_PARTIALY`() {
        //GIVEN
        val data = ITEM_START
        //WHEN
        val result = dumpReader.parseNextItem(data, 0)
        //THEN
        assertThat(result).isEqualTo(ITEM_PARSED_PARTIALY)
        assertThat(dumpReader.strings.last().first).isEqualTo(ITEM_START)
    }

    @Test
    fun `parseNextItem{ITEM_START}{vehicleId}{ITEM_FINISH}{validJson} - return item parsed`() {
        //GIVEN
        val data = listItem
        //WHEN
        val result = dumpReader.parseNextItem(data, 0)
        //THEN
        assertThat(result).isEqualTo(ITEM_PARSED)
        assertThat(dumpReader.strings.last().first).isEqualTo(data)
    }


    @Test
    fun `parseNextItem{ITEM_START}{vehicleId}{ITEM_FINISH}{validJson but with not valid vehicle id} - return item skipped`() {
        //GIVEN
        val data = ITEM_START + "de_mpb_1" + ITEM_FINISH + itemJsonValid
        //WHEN
        val result = dumpReader.parseNextItem(data, 0)
        //THEN
        assertThat(result).isEqualTo(ITEM_SKIPPED)
        assertThat(dumpReader.strings).isEmpty()
    }

    @Test
    fun `parseNextItem{ITEM_START}{vehicleId}{ITEM_FINISH}{not valid json} - return item skipped`() {
        //GIVEN
        val data = ITEM_START + itemVehicleId + ITEM_FINISH + itemJsonNotValid
        //WHEN
        val result = dumpReader.parseNextItem(data, 0)
        //THEN
        assertThat(result).isEqualTo(ITEM_SKIPPED)
        assertThat(dumpReader.strings).isEmpty()
    }

    @Test
    fun `parseNextItem{ITEM_START}{vehicleId}{ITEM_FINISH}{part of json} - return item partial`() {
        //GIVEN
        val data = ITEM_START + itemVehicleId + ITEM_FINISH + itemJsonValid.substring(
            0,
            itemJsonValid.length / 2
        )
        //WHEN
        val result = dumpReader.parseNextItem(data, 0)
        val added = dumpReader.strings.last()
        //THEN
        assertThat(result).isEqualTo(ITEM_PARSED_PARTIALY)
        assertThat(dumpReader.strings).isNotEmpty()
        assertThat(added.first).isEqualTo(data)
    }

    @Test
    fun `doesChunkContainPartOfStartMarkerAtTheEnd for part of the string should return index of begining`() {
        //GIVEN
        val data = "ASD" + ITEM_START.substring(0, ITEM_START.length - 3)
        //WHEN
        val result = dumpReader.doesChunkContainPartOfStartMarkerAtTheEnd(data)
        //THEN
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `doesChunkContainPartOfStartMarkerAtTheEnd for part of the string should return index of if only 1 letter # exists`() {
        //GIVEN
        val data = "afdadsfadsfasdfadsfasdfadsffasdfasASD#"
        //WHEN
        val result = dumpReader.doesChunkContainPartOfStartMarkerAtTheEnd(data)
        //THEN
        assertThat(result).isEqualTo(37)
    }

    @Test
    fun `doesChunkContainPartOfStartMarkerAtTheEnd for part of the string should return -1 if there is no part of the marker`() {
        //GIVEN
        val data = "afdadsfadsfasdfadsfasdfadsffasdfasASD"
        //WHEN
        val result = dumpReader.doesChunkContainPartOfStartMarkerAtTheEnd(data)
        //THEN
        assertThat(result).isEqualTo(-1)
    }

    @Test
    fun `doesChunkContainPartOfStartMarkerAtTheEnd should return -1 if length of a chunk is too small`() {
        //GIVEN
        val data = "sASD"
        //WHEN
        val result = dumpReader.doesChunkContainPartOfStartMarkerAtTheEnd(data)
        //THEN
        assertThat(result).isEqualTo(-1)
    }

    @Test
    fun `doesChunkContainPartOfStartMarkerAtTheEnd should return -1 if ITEM_START with full symbols at the end`() {
        //GIVEN
        val data = "sASDasdasfadsfads" + ITEM_START
        //WHEN
        val result = dumpReader.doesChunkContainPartOfStartMarkerAtTheEnd(data)
        //THEN
        assertThat(result).isEqualTo(-1)
    }

    @Test
    fun `parseNextChunk with garbage return CHUNK_PARSED without strings add`() {
        //GIVEN
        dumpReader.buffer = CharArray(5000) { 0.toChar() }
        //WHEN
        val result = dumpReader.parseNextChunk(CHUNK_PARSED)
        //THEN
        assertThat(result).isEqualTo(CHUNK_PARSED)
        assertThat(dumpReader.strings).isEmpty()
    }

    @Test
    fun `parseNextChunk get all items to strings`() {
        //GIVEN
        val data =
            ITEM_START + itemVehicleId + ITEM_FINISH + itemJsonValid +
                    listItem +
                    listItem +
                    listItem +
                    ITEM_START + itemVehicleId + ITEM_FINISH + itemJsonNotValid
        dumpReader.buffer = data.toCharArray()
        //WHEN
        val result = dumpReader.parseNextChunk(CHUNK_PARSED)
        //THEN
        assertThat(result).isEqualTo(CHUNK_PARSED)
        assertThat(dumpReader.strings).hasSize(4)
    }

    @Test
    fun `parseNextChunk get all items to strings partial took to a parse from a strings if CHUNK_PARSED_LAST_ITEM_PARTIAL`() {
        //GIVEN
        val newVehicle = "ru_t72"
        val data =
            ITEM_FINISH + itemJsonValid.replace(itemVehicleId, newVehicle) +
                    listItem +
                    listItem
        dumpReader.strings.add(Pair(ITEM_START + newVehicle, 1))
        dumpReader.buffer = data.toCharArray()
        //WHEN
        val result =
            dumpReader.parseNextChunk(WTDumpReader.FILE_PARSE_STATE.CHUNK_PARSED_LAST_ITEM_PARTIAL)
        //THEN
        assertThat(result).isEqualTo(CHUNK_PARSED)
        assertThat(dumpReader.strings).hasSize(3)
    }

    @Test
    fun `parseNextChunk partial at the end placed to a strings and returned CHUNK_PARSED_LAST_ITEM_PARTIAL`() {
        //GIVEN
        val data = ITEM_START + itemVehicleId
        dumpReader.buffer = data.toCharArray()
        //WHEN
        val result = dumpReader.parseNextChunk(WTDumpReader.FILE_PARSE_STATE.CHUNK_PARSED)
        //THEN
        assertThat(result).isEqualTo(WTDumpReader.FILE_PARSE_STATE.CHUNK_PARSED_LAST_ITEM_PARTIAL)
        assertThat(dumpReader.strings).hasSize(1)
        assertThat(dumpReader.strings).containsExactly(Pair(data, 0))
    }

    @Test
    fun `splitLineups with one lineup returns 1 list of items`() {
        //GIVEN
        val elements = arrayOf(Pair(listItem, 0), Pair(listItem, 2), Pair(listItem, 3))
        dumpReader.strings.addAll(elements)
        //WHEN
        val result = dumpReader.splitLineups()
        //THEN
        assertThat(result).hasSize(1)
        assertThat(result.first()).hasSize(3)
        assertThat(result.first()).isEqualTo(elements.map { it.first })
    }
    @Test
    fun `splitLineups with two lineups returns 2 list of items`() {
        //GIVEN
        val elements = arrayOf(Pair(listItem, 0), Pair(listItem, 2), Pair(listItem, MAX_DISTANCE_BETWEEN_LINEUPS + 3))
        dumpReader.strings.addAll(elements)
        //WHEN
        val result = dumpReader.splitLineups()
        //THEN
        assertThat(result).hasSize(2)
        assertThat(result.first()).hasSize(2)
        assertThat(result.last()).hasSize(1)
    }



}