package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonLineupConfig
import com.catp.thundersimlineup.BaseTest
import com.catp.thundersimlineup.MockKForToothpick
import com.catp.thundersimlineup.data.db.LineupDatabase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import toothpick.ktp.delegate.inject

class UpdaterTest : BaseTest() {

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var database: LineupDatabase

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var updateProcess: UpdateProcess

    val updater: Updater by inject()

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun assertEverythingInjected() {
        assertThat(updater.db).isNotNull()
        assertThat(updater.db).isEqualTo(database)
        assertThat(updater.updateProcess).isEqualTo(updateProcess)
    }

    @Test
    fun regularCall() {
        //GIVEN
        val json = mockk<JsonLineupConfig>()
        //WHEN
        updater.process(json)
        //THEN
        verify(exactly = 1) { updateProcess.prepare(json) }
        verify(exactly = 1) { database.runInTransaction(updateProcess) }
    }

    @Test
    fun `transaction throws error futher`() {
        //GIVEN
        val json = mockk<JsonLineupConfig>()
        val message = "Ahtung"
        every { database.runInTransaction(any()) } throws Exception(message)
        //WHEN
        assertThrows<Exception>(message) { updater.process(json) }
        //THEN
        verify(exactly = 1) { updateProcess.prepare(json) }
        verify(exactly = 1) { database.runInTransaction(updateProcess) }
    }
}