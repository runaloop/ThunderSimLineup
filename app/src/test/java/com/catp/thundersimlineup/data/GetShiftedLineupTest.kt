package com.catp.thundersimlineup.data

import com.catp.thundersimlineup.BaseTest
import com.catp.thundersimlineup.data.db.entity.LineupCycleEntity
import com.catp.thundersimlineup.data.db.entity.LineupShiftEntity
import com.catp.thundersimlineup.data.db.entity.LineupType
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import toothpick.ktp.delegate.inject


class GetShiftedLineupTest : BaseTest() {

    val getShiftedLineup: GetShiftedLineup by inject()

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun `shifted date is today, the date parameter is the same, return correct lineup for today`() {
        //GIVEN
        val today = LocalDate.now()
        val shift = LineupShiftEntity(2, today)
        val lineups =
            (0..5).map { LineupCycleEntity(it.toString(), LineupType.LOW, it, true, it.toLong()) }

        //WHEN
        val result = getShiftedLineup.process(shift, today, lineups)

        //THEN
        assertThat(result.lineupName).isEqualTo("2")
    }

    @Test
    fun `shifted day is yesterday, and the date is today, return correct lineup`() {
        //GIVEN
        val today = LocalDate.now()
        val shift = LineupShiftEntity(2, today.minusDays(1))
        val lineups =
            (0..5).map { LineupCycleEntity(it.toString(), LineupType.LOW, it, true, it.toLong()) }

        //WHEN
        val result = getShiftedLineup.process(shift, today, lineups)

        //THEN
        assertThat(result.lineupName).isEqualTo("3")
    }

    @Test
    fun `shifted day is yesterday, and the the shift day is more than array size, return correct lineup`() {
        //GIVEN
        val today = LocalDate.now()
        val shift = LineupShiftEntity(2, today.minusDays(6))
        val lineups =
            (0..5).map { LineupCycleEntity(it.toString(), LineupType.LOW, it, true, it.toLong()) }

        //WHEN
        val result = getShiftedLineup.process(shift, today, lineups)

        //THEN
        assertThat(result.lineupName).isEqualTo("2")
    }
}