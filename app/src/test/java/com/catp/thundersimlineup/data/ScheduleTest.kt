package com.catp.thundersimlineup.data

import com.catp.thundersimlineup.BaseTest
import com.catp.thundersimlineup.MockKForToothpick
import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.*
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import toothpick.ktp.delegate.inject
import kotlin.test.assertFailsWith

class ScheduleTest : BaseTest() {

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var dao: LineupDao

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var getShiftedLineup: GetShiftedLineup

    val schedule: Schedule by inject()

    @Before
    override fun setUp() {
        super.setUp()

    }

    @Test
    fun `error throws if lineup shift cant be found in lineupmap`() {
        //GIVEN
        schedule.lineupsMap = mutableMapOf()
        schedule.lineupsMap[LineupType.LOW] = listOf(LineupCycleEntity("1", LineupType.LOW, 0))
        schedule.lineupShift = listOf(LineupShiftEntity(3, LocalDate.MIN))

        //WHEN
        assertFailsWith<IllegalStateException> {
            schedule.getLineupForDate(LocalDate.now(), LineupType.LOW)
        }
    }

    @Test
    fun `getLineupForDate returns correct lineup`() {
        //GIVEN
        every { dao.getLineupCycleList() } returns (0..5).map { LineupCycleEntity(it.toString(), LineupType.LOW, it, true, it+1L) }
        every { dao.getLineupShift() } returns listOf(LineupShiftEntity(1, LocalDate.now(), 723L))
        every { dao.getLineups() } returns listOf(Lineup(LineupEntity("1")))
        every { getShiftedLineup.process(any(), any(), any()) } returns dao.getLineupCycleList()[1]
        schedule.updateRule()

        //WHEN
        val result = schedule.getLineupForDate(LocalDate.now().plusDays(1), LineupType.LOW)

        //THEN
        assertThat(result).isNotNull()
        assertThat(result!!.lineupEntity.name).isEqualTo("1")
    }

    @Test
    fun `trying to find experimental lineup, when no lineupAvailability is null`() {
        //GIVEN
        //WHEN
        val result = schedule.getExperimentalLineupForDate(LocalDate.now())
        //THEN
        assertThat(result).isNull()
        verify { dao wasNot Called}
    }

    @Test
    fun `trying to find experimental lineup, with time before availability`() {
        //GIVEN
        schedule.lineupAvailability = LineupCycleAvailabilityEntity(1, LocalDate.now(), LocalDate.now().plusDays(1))

        //WHEN
        val result = schedule.getExperimentalLineupForDate(LocalDate.now().minusDays(1))

        //THEN
        assertThat(result).isNull()
        verify { dao wasNot Called}
    }
    @Test
    fun `trying to find experimental lineup, with time after availability`() {
        //GIVEN
        schedule.lineupAvailability = LineupCycleAvailabilityEntity(1, LocalDate.now(), LocalDate.now().plusDays(1))

        //WHEN
        val result = schedule.getExperimentalLineupForDate(LocalDate.now().plusDays(2))

        //THEN
        assertThat(result).isNull()
        verify { dao wasNot Called}
    }

    @Test
    fun `find experimental lineup, at certain date`() {
        checkCertainDate(LocalDate.now())
        checkCertainDate(LocalDate.now().plusDays(1))
    }

    private fun checkCertainDate(date:LocalDate){
        //GIVEN
        schedule.lineupAvailability = LineupCycleAvailabilityEntity(1, LocalDate.now(), LocalDate.now().plusDays(1))
        schedule.lineupsMap = mutableMapOf()
        schedule.lineupsMap[LineupType.EXCREMENTAL] = listOf(LineupCycleEntity("E1", LineupType.EXCREMENTAL, 1, true, 1))
        val experimentalLineup = Lineup(LineupEntity("E1"))
        every { dao.getLineups() } returns listOf(experimentalLineup, Lineup(LineupEntity("1")))
        //WHEN
        val result = schedule.getExperimentalLineupForDate(date)
        //THEN
        assertThat(result).isEqualTo(experimentalLineup)
    }
}