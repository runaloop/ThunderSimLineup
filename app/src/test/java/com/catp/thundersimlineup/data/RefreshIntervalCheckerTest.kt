package com.catp.thundersimlineup.data

import android.content.Context
import android.content.SharedPreferences
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import org.threeten.bp.Duration
import org.threeten.bp.Instant

class RefreshIntervalCheckerTest {


    @MockK
    lateinit var context: Context

    @InjectMockKs
    lateinit var refreshIntervalChecker: RefreshIntervalChecker

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `refresh time is not set, returns true`() {
        //GIVEN
        every { context.getSharedPreferences(any(), any()).getString(any(), any()) } returns ""

        //WHEN
        val result = refreshIntervalChecker.isRefreshNeeded(context)

        //THEN
        assertThat(result).isTrue()
    }

    @Test
    fun `refresh time less than refresh period, returns false`() {
        //GIVEN
        mockkStatic(Duration::class)
        mockkStatic(Instant::class)
        every {
            Duration.between(
                any(),
                any()
            )
        } returns RefreshIntervalChecker.REFRESH_INTERVAL.minusMillis(1)
        every { Instant.parse(any()) } returns mockk<Instant>()
        every { context.getSharedPreferences(any(), any()).getString(any(), any()) } returns "TIME"

        //WHEN
        val result = refreshIntervalChecker.isRefreshNeeded(context)

        //THEN
        assertThat(result).isFalse()
    }

    @Test
    fun `refresh time more than refresh period, returns true`() {
        //GIVEN
        mockkStatic(Duration::class)
        mockkStatic(Instant::class)
        every {
            Duration.between(
                any(),
                any()
            )
        } returns RefreshIntervalChecker.REFRESH_INTERVAL.plusMillis(1)
        every { Instant.parse(any()) } returns mockk<Instant>()
        every { context.getSharedPreferences(any(), any()).getString(any(), any()) } returns "TIME"

        //WHEN
        val result = refreshIntervalChecker.isRefreshNeeded(context)

        //THEN
        assertThat(result).isTrue()
    }

    @Test
    fun `setRefreshed sets current time`() {
        //GIVEN
        mockkStatic(Instant::class)
        val time = "ttttime"
        every { Instant.now().toString() } returns time
        val editor = mockk<SharedPreferences.Editor> {
            every { putString(any(), any()) } returns this
            every { commit() } returns true
        }
        every {
            context.getSharedPreferences(REFRESH_PREFERENCE, Context.MODE_PRIVATE).edit()
        } returns editor


        //WHEN
        refreshIntervalChecker.setRefreshed(context)

        //THEN
        verifyOrder {
            editor.putString(REFRESH_LAST_TIME, time)
            editor.commit()
        }
    }
}