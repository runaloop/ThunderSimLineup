package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonLineup
import com.catp.model.JsonLineupConfig
import com.catp.model.JsonRules
import com.catp.model.JsonVehicleStore
import com.catp.thundersimlineup.BaseTest
import com.catp.thundersimlineup.MockKForToothpick
import com.catp.thundersimlineup.data.db.LineupDao
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import toothpick.ktp.delegate.inject

class UpdateProcessTest : BaseTest() {
    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var dao: LineupDao

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var updateVehicleStore: UpdateVehicleStore

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var updateLineupsTeams: UpdateTeams

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var updateVehicleCrossRef: UpdateVehicleCrossRef

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var updateVehicleCrossRefStatus: UpdateVehicleCrossRefStatus

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var updateLineupCycle: UpdateLineupCycle

    val updateProcess: UpdateProcess by inject()

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun assertEverythingInjected() {
        assertThat(updateProcess.updateLineupCycle).isNotNull()
        assertThat(updateProcess.updateLineupCycle).isEqualTo(updateLineupCycle)
        assertThat(updateProcess.updateVehicleCrossRefStatus).isEqualTo(updateVehicleCrossRefStatus)
        assertThat(updateProcess.updateVehicleCrossRef).isEqualTo(updateVehicleCrossRef)
        assertThat(updateProcess.updateLineupsTeams).isEqualTo(updateLineupsTeams)
        assertThat(updateProcess.updateVehicleStore).isEqualTo(updateVehicleStore)
        assertThat(updateProcess.lineupDao).isEqualTo(dao)
    }

    @Test
    fun regularRun() {
        //GIVEN
        val jsonLineups = listOf<JsonLineup>()
        val jsonVehicleStore = JsonVehicleStore()
        val jsonRules = JsonRules()
        val version = 123
        val json = JsonLineupConfig(jsonLineups, jsonVehicleStore, jsonRules, version)
        //WHEN
        updateProcess.prepare(json)
        updateProcess.run()
        //THEN
        verify(exactly = 1) { updateVehicleStore.process(jsonVehicleStore) }
        verify(exactly = 1) { updateLineupsTeams.process(jsonLineups) }
        verify(exactly = 1) { updateVehicleCrossRefStatus.process() }
        verify(exactly = 1) { updateVehicleCrossRef.process(jsonLineups) }
        verify(exactly = 1) { updateLineupCycle.process(jsonRules) }
        verify(exactly = 1) { dao.setVersion(version) }
    }


    @Test
    fun `exception while process throws further`() {
        //GIVEN
        val message = "adfadf"
        every { updateVehicleStore.process(any()) } throws Exception(message)
        //WHEN
        val jsonVehicleStore = JsonVehicleStore()
        val json = JsonLineupConfig(listOf(), jsonVehicleStore, JsonRules())
        updateProcess.prepare(json)
        assertThrows<Exception>(message) {
            updateProcess.run()
        }
        //THEN

        verify(exactly = 1) { updateVehicleStore.process(jsonVehicleStore) }
        verify {
            listOf(
                updateLineupsTeams,
                updateVehicleCrossRefStatus,
                updateVehicleCrossRef,
                updateLineupCycle,
                dao
            ) wasNot Called
        }
    }
}