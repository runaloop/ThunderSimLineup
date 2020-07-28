package com.catp.thundersimlineup.data.db

import android.content.Context
import com.catp.model.JsonLineupConfig
import com.catp.model.JsonRules
import com.catp.model.JsonVehicleStore
import com.catp.thundersimlineup.BaseTest
import com.catp.thundersimlineup.MockKForToothpick
import com.catp.thundersimlineup.data.Preferences
import com.catp.thundersimlineup.data.db.operation.AggressiveUpdater
import com.catp.thundersimlineup.data.db.operation.Updater
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import toothpick.ktp.delegate.inject


class DBPopulateTest : BaseTest() {


    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var preferences: Preferences

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var changes: Changeset

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var context: Context

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var updater: Updater

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var aggressiveUpdater: AggressiveUpdater

    val dbPopulate: DBPopulate by inject()

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun assertEverythingInjected() {
        assertThat(dbPopulate.updater).isNotNull()
        assertThat(dbPopulate.updater).isEqualTo(updater)
        assertThat(dbPopulate.aggressiveUpdater).isEqualTo(aggressiveUpdater)
        assertThat(dbPopulate.preferences).isEqualTo(preferences)
        assertThat(dbPopulate.changes).isEqualTo(changes)
    }

    @Test
    fun regularLaunch() {
        //GIVEN
        every { preferences.logVehicleEvents } returns true
        val json = JsonLineupConfig(listOf(), JsonVehicleStore(), JsonRules(), 1)
        //WHEN
        dbPopulate.updateData(json)
        //THEN
        verify(exactly = 1) { updater.process(json) }
        verify(exactly = 1) { changes.writeChanges() }
        verify { aggressiveUpdater wasNot Called }
    }

    @Test
    fun `reg launch but changeset forbidden`() {
        //GIVEN
        every { preferences.logVehicleEvents } returns false
        val json = JsonLineupConfig(listOf(), JsonVehicleStore(), JsonRules(), 1)
        //WHEN
        dbPopulate.updateData(json)
        //THEN
        verify(exactly = 1) { updater.process(json) }
        verify { changes wasNot Called }
        verify { aggressiveUpdater wasNot Called }
    }

    @Test
    fun `exception while update, calling aggressiveUpdater, and everything succeed`() {
        //GIVEN
        every { preferences.logVehicleEvents } returns true
        val exception = Exception()
        every { updater.process(any()) } throws exception
        val json = JsonLineupConfig(listOf(), JsonVehicleStore(), JsonRules(), 1)
        //WHEN
        dbPopulate.updateData(json)
        //THEN
        verify(exactly = 1) { updater.process(json) }
        verify(exactly = 1) { aggressiveUpdater.process(json, exception) }
        verify(exactly = 1) { changes.writeChanges() }
    }

    @Test
    fun `exception while update, calling aggressiveUpdater and throwing error again, no changes called, error throwed futher`() {
        //GIVEN
        every { preferences.logVehicleEvents } returns true
        val exception = Exception()
        val aggressiveMessage = "Ahtung"
        val exception2 = Exception(aggressiveMessage)
        every { updater.process(any()) } throws exception
        every { aggressiveUpdater.process(any(), any()) } throws exception2

        val json = JsonLineupConfig(listOf(), JsonVehicleStore(), JsonRules(), 1)
        //WHEN
        assertThrows<Exception>(aggressiveMessage) {
            dbPopulate.updateData(json)
        }
        //THEN
        verify(exactly = 1) { updater.process(json) }
        verify(exactly = 1) { aggressiveUpdater.process(json, exception) }
        verify(exactly = 1) { changes.clear() }
        verify(exactly = 0) { changes.writeChanges() }
    }
}