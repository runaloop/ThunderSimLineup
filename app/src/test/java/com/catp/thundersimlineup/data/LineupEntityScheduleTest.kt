package com.catp.thundersimlineup.data

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.catp.thundersimlineup.App
import com.catp.thundersimlineup.lShift
import com.catp.thundersimlineup.rShift
import com.google.common.truth.Truth.assertThat
import com.jakewharton.threetenabp.AndroidThreeTen
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(application = App::class, sdk = [21])
@RunWith(RobolectricTestRunner::class)
internal class LineupEntityScheduleTest {


    @Before
    fun setUp() {
        //AndroidThreeTen.init(getApplicationContext<App>())
    }


    @Test
    fun `rshift to 1 works`() {
        val data = listOf(1,2,3)
        assertThat(data.rShift(1)).isEqualTo(listOf(3,1,2))
    }

    @Test
    fun `rshift more than size of list works`() {
        val data = listOf(1,2,3)
        assertThat(data.rShift(4)).isEqualTo(listOf(3,1,2))
    }

    @Test
    fun `lshift to 1 works`() {
        val data = listOf(1,2,3,4,5)
        assertThat(data.lShift(1)).isEqualTo(listOf(2,3,4,5,1))
    }

    @Test
    fun `lshift more than size of list works`() {
        val data = listOf(1,2,3,4,5)
        assertThat(data.lShift(10)).isEqualTo(listOf(1,2,3,4,5))
    }
}