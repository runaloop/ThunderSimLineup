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


    private val getShiftedLineup: GetShiftedLineup by inject()

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

    @Test
    fun `9_2 8_2 10_2 order is not worked anymore`() {
        val today10 = LocalDate.of(2020, 7, 10)
        //val shift_date = LocalDate.of(2020, 6, 10)
        val shiftDate = LocalDate.of(2020, 7, 7)
        val shiftEntity = LineupShiftEntity(3, shiftDate)
        val lineups = listOf("8_2", "10_2", "8_2_2", "9_2").mapIndexed { index, s ->
            LineupCycleEntity(
                s,
                LineupType.TOP,
                index,
                true,
                index.toLong()
            )
        }
        println(getShiftedLineup.process(shiftEntity, today10, lineups))
        println(getShiftedLineup.process(shiftEntity, today10.minusDays(1), lineups))
        println(getShiftedLineup.process(shiftEntity, today10.minusDays(2), lineups))
        println(getShiftedLineup.process(shiftEntity, today10.minusDays(3), lineups))
    }
}