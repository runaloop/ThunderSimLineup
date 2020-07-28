package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonLineupConfig
import com.catp.model.JsonRules
import com.catp.model.JsonVehicleStore
import com.catp.thundersimlineup.BaseTest
import com.catp.thundersimlineup.MockKForToothpick
import com.catp.thundersimlineup.data.db.LineupDatabase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import toothpick.ktp.delegate.inject

class AggressiveUpdaterTest : BaseTest() {

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var errorReporter: ErrorReporter

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var favorites: FavoriteSaver

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var updater: Updater

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var db: LineupDatabase

    val agUpdater: AggressiveUpdater by inject()

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun assertEverythingInjected() {
        assertThat(agUpdater.db).isNotNull()
        assertThat(agUpdater.db).isEqualTo(db)
        assertThat(agUpdater.updater).isEqualTo(updater)
        assertThat(agUpdater.favorites).isEqualTo(favorites)
        assertThat(agUpdater.errorReporter).isEqualTo(errorReporter)
    }

    @Test
    fun regularRun() {
        //GIVEN
        val error = Exception()
        val json = JsonLineupConfig(listOf(), JsonVehicleStore(), JsonRules())

        //WHEN
        agUpdater.process(json, error)

        //THEN
        verify(exactly = 1) { errorReporter.process(error, any()) }
        verify(exactly = 1) { favorites.save() }
        verify(exactly = 1) { db.clearAllTables() }
        verify(exactly = 1) { updater.process(json) }
        verify(exactly = 1) { favorites.restore() }
    }

    @Test
    fun `update fails throw error further`() {
        //GIVEN
        val error = Exception()
        val message = "Exception()"
        val error2 = Exception()
        val json = JsonLineupConfig(listOf(), JsonVehicleStore(), JsonRules())
        every { updater.process(json) } throws error2

        //WHEN
        assertThrows<Exception>(message) {
            agUpdater.process(json, error)
        }

        //THEN
        verify(exactly = 1) { errorReporter.process(error, any()) }
        verify(exactly = 1) { errorReporter.process(error2, any()) }
        verify(exactly = 1) { favorites.save() }
        verify(exactly = 1) { db.clearAllTables() }
        verify(exactly = 1) { updater.process(json) }
        verify(exactly = 0) { favorites.restore() }
    }

    //updater.process make error throw further
}