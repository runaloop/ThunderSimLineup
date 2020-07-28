package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonVehicle
import com.catp.model.VehicleType
import com.catp.thundersimlineup.data.db.Changeset
import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class CheckAndUpdateBRTest {
    @MockK(relaxed = true)
    lateinit var changeset: Changeset

    @InjectMockKs
    lateinit var checkAndUpdateBR: CheckAndUpdateBR


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `checkAndUpdateBR if BR is equal, doesnot update and returns false`() {
        val result = checkAndUpdateBR.process(
            Vehicle("", VehicleType.HELI, "", "", "1.7", false),
            JsonVehicle("", VehicleType.HELI, "", "1.7")
        )

        assertThat(result).isFalse()
        verify { changeset wasNot Called }
    }

    @Test
    fun `checkAndUpdateBR if BR is not equal, updates data and returns true`() {
        val oldBR = "1.7"
        val vehicle = Vehicle("", VehicleType.HELI, "", "", oldBR, false)
        val newBR = "2.7"
        val jsonVehicle = JsonVehicle("", VehicleType.HELI, "", newBR)
        val result = checkAndUpdateBR.process(
            vehicle,
            jsonVehicle
        )

        assertThat(result).isTrue()
        assertThat(vehicle.br).isEqualTo(newBR)
    }
}