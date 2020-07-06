package com.catp.thundersimlineup.ui.lineuplist

import com.catp.model.JsonRules.Companion.LINEUP_UTC_TIME_OF_CHANGE
import com.catp.thundersimlineup.BaseTest
import com.catp.thundersimlineup.LocalDateTimeProvider
import com.catp.thundersimlineup.MockKForToothpick
import com.catp.thundersimlineup.data.Schedule
import com.catp.thundersimlineup.data.db.entity.Lineup
import com.catp.thundersimlineup.data.db.entity.LineupType
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import toothpick.ktp.delegate.inject

class LineupRequestInteractorTest : BaseTest() {
    val interactor: LineupRequestInteractor by inject()

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var lineupSchedule: Schedule

    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var localDateTimeProvider: LocalDateTimeProvider

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun `everythingInjected`() {
        assertThat(interactor).isNotNull()
        assertThat(interactor.lineupSchedule).isNotNull()
        assertThat(interactor.lineupSchedule).isEqualTo(lineupSchedule)
    }

    @Test
    fun `If day is day is NOT today, return lineup for that day and +1 day, with zero diff`() {
        //GIVEN
        val date = LocalDate.now().minusDays(5)
        val lowLineup = mockk<Lineup>()
        val topLineup = mockk<Lineup>()
        val lowLineup2 = mockk<Lineup>()
        val topLineup2 = mockk<Lineup>()
        every { lineupSchedule.getLineupForDate(any(), any()) } returns null
        every { lineupSchedule.getLineupForDate(eq(date), LineupType.LOW) }.returns(lowLineup)
        every { lineupSchedule.getLineupForDate(eq(date), LineupType.TOP) }.returns(topLineup)
        every { lineupSchedule.getLineupForDate(neq(date), LineupType.LOW) }.returns(lowLineup2)
        every { lineupSchedule.getLineupForDate(neq(date), LineupType.TOP) }.returns(topLineup2)

        //WHEN
        val (day, after, diff) = interactor.getLineupForADay(date)

        //THEN
        assertThat(day.first).isEqualTo(lowLineup)
        assertThat(day.second).isEqualTo(topLineup)
        assertThat(after.first).isEqualTo(lowLineup2)
        assertThat(after.second).isEqualTo(topLineup2)
        assertThat(diff).isEqualTo(Duration.ZERO)
    }

    @Test
    fun `If the day is today, and time is before lineup change, return day-1 and today lineups, with diff to change`() {
        //GIVEN
        val lowLineup = mockk<Lineup>()
        val topLineup = mockk<Lineup>()
        val lowLineup2 = mockk<Lineup>()
        val topLineup2 = mockk<Lineup>()
        val date = LocalDate.now()
        every { localDateTimeProvider.now() } returns LocalDateTime.now().withHour(LINEUP_UTC_TIME_OF_CHANGE-1).withMinute(0).withSecond(0)
        every { lineupSchedule.getLineupForDate(any(), any()) } returns null
        every { lineupSchedule.getLineupForDate(eq(date.minusDays(1)), LineupType.LOW) }.returns(lowLineup)
        every { lineupSchedule.getLineupForDate(eq(date.minusDays(1)), LineupType.TOP) }.returns(topLineup)
        every { lineupSchedule.getLineupForDate(eq(date), LineupType.LOW) }.returns(lowLineup2)
        every { lineupSchedule.getLineupForDate(eq(date), LineupType.TOP) }.returns(topLineup2)
        //WHEN
        val (day, after, diff) = interactor.getLineupForADay(LocalDate.now())

        //THEN
        assertThat(day.first).isEqualTo(lowLineup)
        assertThat(day.second).isEqualTo(topLineup)
        assertThat(after.first).isEqualTo(lowLineup2)
        assertThat(after.second).isEqualTo(topLineup2)
        assertThat(diff).isEqualTo(Duration.ofHours(1))
    }

    @Test
    fun `If the day is today, and time is after lineup change, return today and today+1 lineups, with diff to change`() {
        //GIVEN
        val lowLineup = mockk<Lineup>()
        val topLineup = mockk<Lineup>()
        val lowLineup2 = mockk<Lineup>()
        val topLineup2 = mockk<Lineup>()
        val date = LocalDate.now()
        every { localDateTimeProvider.now() } returns LocalDateTime.now().withHour(LINEUP_UTC_TIME_OF_CHANGE+1).withMinute(0).withSecond(0)
        every { lineupSchedule.getLineupForDate(any(), any()) } returns null
        every { lineupSchedule.getLineupForDate(eq(date), LineupType.LOW) }.returns(lowLineup)
        every { lineupSchedule.getLineupForDate(eq(date), LineupType.TOP) }.returns(topLineup)
        every { lineupSchedule.getLineupForDate(eq(date.plusDays(1)), LineupType.LOW) }.returns(lowLineup2)
        every { lineupSchedule.getLineupForDate(eq(date.plusDays(1)), LineupType.TOP) }.returns(topLineup2)
        //WHEN
        val (day, after, diff) = interactor.getLineupForADay(LocalDate.now())

        //THEN
        assertThat(day.first).isEqualTo(lowLineup)
        assertThat(day.second).isEqualTo(topLineup)
        assertThat(after.first).isEqualTo(lowLineup2)
        assertThat(after.second).isEqualTo(topLineup2)
        assertThat(diff).isEqualTo(Duration.ofHours(23))
    }



}