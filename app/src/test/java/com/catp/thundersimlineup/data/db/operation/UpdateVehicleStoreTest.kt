package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonLocaleItem
import com.catp.model.JsonVehicle
import com.catp.model.JsonVehicleStore
import com.catp.model.VehicleType
import com.catp.thundersimlineup.data.db.Changeset
import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.Vehicle
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

internal class UpdateVehicleStoreTest {

    @MockK(relaxed = true)
    lateinit var dao: LineupDao

    @MockK(relaxed = true)
    lateinit var changeset: Changeset
    @MockK(relaxed = true)
    lateinit var checkAndUpdateBR: CheckAndUpdateBR
    @MockK(relaxed = true)
    lateinit var checkAndUpdateTitle: CheckAndUpdateTitle

    @InjectMockKs
    lateinit var updateVehicleStore: UpdateVehicleStore

    lateinit var vehicleInDB: Vehicle
    lateinit var jsonVehicle: JsonVehicle
    val vehicleId = "first"
    val vehicleType = VehicleType.HELI
    val nation = "ru"
    val br = "1.1"
    val fullEnglishTitle = "fulltitle"

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        jsonVehicle = JsonVehicle(
            vehicleId, vehicleType, nation, br,
            JsonLocaleItem(vehicleId, "", "", fullEnglishTitle, "", nation)
        )
        vehicleInDB = Vehicle.fromJson(jsonVehicle)
    }

    @Test
    fun `db is empty, fill all data from json`() {
        //GIVEN
        every { dao.getVehicles() } returns emptyList()
        val vehicleStore = JsonVehicleStore(mutableListOf(jsonVehicle))

        //WHEN
        updateVehicleStore.process(vehicleStore)

        //THEN
        verify { listOf(checkAndUpdateBR, checkAndUpdateTitle) wasNot Called }
        verify { dao.upsertVehicles(eq(listOf(vehicleInDB))) }
    }

    @Test
    fun `db is filled, and equal to json data, no write operation performed`() {
        //GIVEN
        every { dao.getVehicles() } returns listOf(vehicleInDB)
        every { checkAndUpdateBR.process(any(), any()) } returns false
        every { checkAndUpdateTitle.process(any(), any()) } returns false

        //WHEN
        updateVehicleStore.process(JsonVehicleStore(mutableListOf(jsonVehicle)))

        //THEN
        verify(exactly = 0) { dao.upsertVehicles(any()) }
    }

    @Test
    fun `db is filled, but json has new vehicles, insert it to db`() {
        //GIVEN
        every { dao.getVehicles() } returns listOf(vehicleInDB)
        every { checkAndUpdateBR.process(any(), any()) } returns false
        every { checkAndUpdateTitle.process(any(), any()) } returns false
        val newJsonVehicle = JsonVehicle(
            "newname", VehicleType.HELI, "ru", "2.2",
            JsonLocaleItem("newname", "", "", "fulltitle2", "", "ru")
        )
        val newVehicle = Vehicle.fromJson(newJsonVehicle)

        //WHEN
        updateVehicleStore.process(JsonVehicleStore(mutableListOf(jsonVehicle, newJsonVehicle)))

        //THEN
        verify { dao.upsertVehicles(eq(listOf(newVehicle))) }
    }

    @Test
    fun `db is filled, but json has updated vehicles by title and br, update data`() {
        //GIVEN
        every { dao.getVehicles() } returns listOf(vehicleInDB)
        every { checkAndUpdateBR.process(any(), any()) } returns true
        every { checkAndUpdateTitle.process(any(), any()) } returns true

        //WHEN
        updateVehicleStore.process(JsonVehicleStore(mutableListOf(jsonVehicle)))

        //THEN
        verify { dao.upsertVehicles(eq(listOf(vehicleInDB))) }
    }
}