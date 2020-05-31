package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonLineup
import com.catp.model.JsonTeam
import com.catp.thundersimlineup.data.db.Changeset
import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.LineupEntity
import com.catp.thundersimlineup.data.db.entity.TeamWithVehicleCrossRef
import com.catp.thundersimlineup.data.db.entity.VehicleStatus
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class UpdateVehicleCrossRefTest {
    @MockK(relaxed = true)
    lateinit var dao: LineupDao
    @MockK(relaxed = true)
    lateinit var changeset: Changeset
    @MockK(relaxed = true)
    lateinit var findNewVehicles: FindNewVehicles
    @MockK(relaxed = true)
    lateinit var findVehiclesToDelete: FindVehiclesToDelete
    @InjectMockKs
    lateinit var updateVehicleCrossRef: UpdateVehicleCrossRef

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }


    @Test
    fun `lineup not found in db, throws error`() {
        //GIVEN
        val jsonLineups = listOf(JsonLineup("1_1"))
        every { dao.getLineupsEntity() } returns emptyList()
        //WHEN
        assertFailsWith<IllegalStateException> {
            updateVehicleCrossRef.process(jsonLineups)
        }
    }

    @Test
    fun `empty db, insert new vehicle refs`() {
        //GIVEN
        val lineupName = "1_1"
        val vehicle1Name = "bmp"
        val vehicle2Name = "kpz"
        val jsonLineups = listOf(
            JsonLineup(
                lineupName,
                JsonTeam(mutableListOf(vehicle1Name)),
                JsonTeam(mutableListOf(vehicle2Name))
            )
        )
        val vehicle1 = TeamWithVehicleCrossRef(1, vehicle1Name, VehicleStatus.NEW)
        val vehicle2 = TeamWithVehicleCrossRef(2, vehicle2Name, VehicleStatus.NEW)
        every { dao.getLineupsEntity() } returns listOf(LineupEntity(lineupName, 1, 2))
        every { dao.getTeamWithVehicleCrossRef() } returns emptyList()
        every { findNewVehicles.process(any(), any(), eq(1)) } returns listOf(
            vehicle1
        )
        every { findNewVehicles.process(any(), any(), eq(2)) } returns listOf(
            vehicle2
        )

        //WHEN
        updateVehicleCrossRef.process(jsonLineups)

        //THEN
        verify { dao.insertTeamWithVehicleCrossRef(eq(listOf(vehicle1, vehicle2))) }
        verify(exactly = 0) { dao.updateVehicleCrossRef(any()) }
    }

    @Test
    fun `filled db, and same data in json, do nothing`() {
        //GIVEN
        val lineupName = "1_1"
        val vehicle1Name = "bmp"
        val vehicle2Name = "kpz"
        val jsonLineups = listOf(
            JsonLineup(
                lineupName,
                JsonTeam(mutableListOf(vehicle1Name)),
                JsonTeam(mutableListOf(vehicle2Name))
            )
        )
        every { dao.getLineupsEntity() } returns listOf(LineupEntity(lineupName, 1, 2))
        every { dao.getTeamWithVehicleCrossRef() } returns emptyList()
        every { findNewVehicles.process(any(), any(), any()) } returns emptyList()
        every { findVehiclesToDelete.process(any(), any()) } returns emptyList()

        //WHEN
        updateVehicleCrossRef.process(jsonLineups)

        //THEN
        verify(exactly = 0) {
            dao.insertTeamWithVehicleCrossRef(any())
            dao.updateVehicleCrossRef(any())
        }
    }

    @Test
    fun `filled db, json has one updated item, update data`() {
        //GIVEN
        val lineupName = "1_1"
        val vehicle1Name = "bmp"
        val vehicle2Name = "kpz"
        val jsonLineups = listOf(
            JsonLineup(
                lineupName,
                JsonTeam(mutableListOf(vehicle1Name)),
                JsonTeam(mutableListOf(vehicle2Name))
            )
        )
        val vehicle = TeamWithVehicleCrossRef(2, vehicle2Name, VehicleStatus.DELETED)
        every { dao.getLineupsEntity() } returns listOf(LineupEntity(lineupName, 1, 2))
        every { dao.getTeamWithVehicleCrossRef() } returns emptyList()
        every { findVehiclesToDelete.process(any(), any()) } returns listOf(
            vehicle
        )

        //WHEN
        updateVehicleCrossRef.process(jsonLineups)

        //THEN
        verify(exactly = 0) {
            dao.insertTeamWithVehicleCrossRef(any())
        }
        verify {
            dao.updateVehicleCrossRef(
                eq(
                    listOf(
                        vehicle,
                        vehicle
                    )
                )
            )
        }// double cause findVehcilesToDelete calls two times
    }
}