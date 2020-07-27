package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonLocaleItem
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

class CheckAndUpdateTitleTest {
    @MockK(relaxed = true)
    lateinit var changeset: Changeset

    @InjectMockKs
    lateinit var checkAndUpdateTitle: CheckAndUpdateTitle


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `checkAndUpdateTitle if title is equal, doesnot update and returns false`() {
        val title = "sometitle"
        val result = checkAndUpdateTitle.process(
            Vehicle("", VehicleType.HELI, "", title, "", false),
            JsonVehicle(
                "", VehicleType.HELI, "", "",
                JsonLocaleItem("", title)
            )
        )

        assertThat(result).isFalse()
        verify { changeset wasNot Called }
    }

}