package com.catp.thundersimlineup

import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.Rule
import org.threeten.bp.zone.TzdbZoneRulesProvider
import org.threeten.bp.zone.ZoneRulesProvider
import toothpick.testing.ToothPickRule

@Suppress("LeakingThis")
open class BaseTest {
    @get:Rule
    var toothPickRule = ToothPickRule(this, "test scope")

    private fun initThreeTen() {
        if (ZoneRulesProvider.getAvailableZoneIds().isEmpty()) {
            val stream = this.javaClass.classLoader!!.getResourceAsStream("TZDB.dat")
            stream.use(::TzdbZoneRulesProvider).apply {
                ZoneRulesProvider.registerProvider(this)
            }
        }
    }

    @Before
    open fun setUp() {
        initThreeTen()
        MockKAnnotations.init(this)
        toothPickRule.inject(this)
    }
}