package com.catp.thundersimlineup.data.db.operation

import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.TeamWithVehicleCrossRef
import com.catp.thundersimlineup.data.db.entity.VehicleStatus
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class UpdateVehicleCrossRefStatusTest {
    @MockK(relaxed = true)
    lateinit var dao: LineupDao
    @InjectMockKs
    lateinit var updateVehicleCrossRefStatus: UpdateVehicleCrossRefStatus

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `empty db, do nothing`() {
        //GIVEN
        every { dao.getTeamWithVehicleCrossRef() } returns emptyList()
        //WHEN
        updateVehicleCrossRefStatus.process()
        //THEN
        verify(exactly = 0) {
            dao.deleteVehicleCrossRef(any())
            dao.updateVehicleCrossRef(any())
        }
    }

    @Test
    fun `no vehicles to delete, and no vehicles with new status - do nothing`() {
        //GIVEN
        every { dao.getTeamWithVehicleCrossRef() } returns listOf(TeamWithVehicleCrossRef(1, "a"))
        //WHEN
        updateVehicleCrossRefStatus.process()
        //THEN
        verify(exactly = 0) {
            dao.deleteVehicleCrossRef(any())
            dao.updateVehicleCrossRef(any())
        }
    }

    @Test
    fun `vehicle with delete state, delete it from db`() {
        //GIVEN
        val toDelete = TeamWithVehicleCrossRef(1, "a", VehicleStatus.DELETED)
        every { dao.getTeamWithVehicleCrossRef() } returns listOf(
            toDelete,
            TeamWithVehicleCrossRef(2, "b", VehicleStatus.REGULAR)
        )
        //WHEN
        updateVehicleCrossRefStatus.process()
        //THEN
        verify {
            dao.deleteVehicleCrossRef(eq(listOf(toDelete)))
        }
        verify(exactly = 0) {
            dao.updateVehicleCrossRef(any())
        }
    }

    @Test
    fun `few vehicles with new state, update it to regular`() {
        //GIVEN
        val toDrop = TeamWithVehicleCrossRef(1, "a", VehicleStatus.NEW)
        every { dao.getTeamWithVehicleCrossRef() } returns listOf(
            toDrop,
            TeamWithVehicleCrossRef(2, "b", VehicleStatus.REGULAR)
        )
        //WHEN
        updateVehicleCrossRefStatus.process()
        //THEN
        verify {
            dao.updateVehicleCrossRef(eq(listOf(toDrop)))
        }
        verify(exactly = 0) {
            dao.deleteVehicleCrossRef(any())
        }
    }

}