package com.catp.thundersimlineup.data

import android.content.Context
import com.catp.model.JsonLineupConfig
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LineupEntityStorageTest {

    @MockK
    lateinit var netJsonLineupConfig: JsonLineupConfig

    @MockK
    lateinit var diskJsonLineupConfig: JsonLineupConfig

    @MockK
    lateinit var netLoader: NetLoader

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var storage: Storage

    @MockK(relaxed = true)
    lateinit var refreshIntervalChecker: RefreshIntervalChecker

    @InjectMockKs
    lateinit var lineupStorage: LineupStorage


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        /*every { netLoader.getData() } returns netLineupConfig
        every { storage.loadLocalData(any()) } returns diskLineupConfig*/
    }

    @Test
    fun `refresh is not occurred cause of refresh interval checker`() {
        //GIVEN
        every { refreshIntervalChecker.isRefreshNeeded(context) } returns false
        //WHEN
        val result = lineupStorage.refresh(context, false)

        //THEN
        verify { listOf(netLoader, storage) wasNot Called }
    }

    @Test
    fun `refresh is occurred cause of force parameter even if interval checker is false`() {
        //GIVEN
        every { refreshIntervalChecker.isRefreshNeeded(context) } returns false

        //WHEN
        val result = lineupStorage.refresh(context, true)

        //THEN
        verify { netLoader.getData() }
    }

    @Test
    fun `refresh is occurred cause of refresh interval checker`() {
        //GIVEN
        every { refreshIntervalChecker.isRefreshNeeded(context) } returns true

        //WHEN
        val result = lineupStorage.refresh(context, false)

        //THEN
        verify { netLoader.getData() }
    }


    @Test
    fun `refresh with empty local db and empty network result reads local json and save it to db`() {

    }

    @Test
    fun `refresh with empty local db and success network call, version of network call is newer than local, update local db`() {
        /*//GIVEN
        val lineup = Lineup("1")
        every { diskStorage.saveLineup(any(), any()) } just Runs
        every { refreshIntervalChecker.isRefreshNeeded(context) } returns true
        every { lineupMerger.merge(any(), any()) } answers { listOf(lineup) }

        //WHEN
        val result = lineupStorage.refresh(context, false)

        //THEN
        verify { diskStorage.saveLineup(context, listOf(lineup)) }
        verify { refreshIntervalChecker.setRefreshed(context) }

        assertThat(result).isEqualTo(listOf(lineup.lineup))*/
    }

    @Test
    fun `refresh with filled local db and empty network result is not saving result to db`() {
        /*//GIVEN
        val lineup = listOf(com.catp.localdataconfigurator.LineupConfigurator("1"))
        every { netLoader.getData() } returns ("")
        every { diskStorage.loadLocalData(any()) } returns lineup.map { it.lineup }
        every { refreshIntervalChecker.isRefreshNeeded(context) } returns true

        //WHEN
        val result = lineupStorage.refresh(context, false)

        //THEN
        assertThat(result).isEmpty()
        verify(exactly = 0) {
            diskStorage.saveLineup(any(), any())
            refreshIntervalChecker.setRefreshed(context)
        }*/
    }

    @Test
    fun `refresh with filled local db and filled same network result is not save result`() {
        /*//GIVEN
        val lineup = listOf(com.catp.localdataconfigurator.LineupConfigurator("1"))
        every { netLoader.getData() } returns ("")
        every { diskStorage.loadLocalData(any()) } returns lineup.map { it.lineup }
        every { refreshIntervalChecker.isRefreshNeeded(context) } returns true

        //WHEN
        val result = lineupStorage.refresh(context, false)

        //THEN
        assertThat(result).isEmpty()
        verify(exactly = 0) {
            diskStorage.saveLineup(any(), any())
            refreshIntervalChecker.setRefreshed(context)
        }*/
    }

    @Test
    fun `refresh with filled local db and filled modified network result correctly merged and saved to db`() {
        /*//GIVEN
        val lineup = com.catp.localdataconfigurator.LineupConfigurator("1")
        lineup.addVehicle("BMP")
        every { netLoader.getData() } returns ("")
        every { diskStorage.loadLocalData(any()) } returns listOf(
            com.catp.localdataconfigurator.LineupConfigurator(
                "1"
            ).lineup)
        every { diskStorage.saveLineup(any(), any()) } just Runs
        every { refreshIntervalChecker.isRefreshNeeded(context) } returns true
        every { lineupMerger.merge(any(), any()) } answers { listOf(lineup.lineup) }

        //WHEN
        val result = lineupStorage.refresh(context, false)

        //THEN
        verify {
            diskStorage.saveLineup(context, listOf(lineup.lineup))
            refreshIntervalChecker.setRefreshed(context)
        }
        assertThat(result).isEqualTo(listOf(lineup.lineup))*/
    }

    @Test
    fun `refresh with filled local db, and modified local json merges data to db`() {
    }

    @Test
    fun `refresh with filled local db, and same version in local json, do nothing`() {
    }
}