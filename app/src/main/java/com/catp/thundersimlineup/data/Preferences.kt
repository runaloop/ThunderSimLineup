package com.catp.thundersimlineup.data

import android.app.Application
import androidx.preference.PreferenceManager
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.ui.lineuplist.LineupListViewModel
import toothpick.InjectConstructor
import java.util.logging.Filter
import javax.inject.Inject

@InjectConstructor
class Preferences {

    @Inject
    lateinit var context: Application

    private fun getBoolean(res: Int): Boolean {
        val boolean = PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(
                context.resources.getString(res), true
            )
        return boolean
    }

    private fun getSet(res: Int): MutableSet<String> {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getStringSet(
                context.resources.getString(res), emptySet()
            )!!
    }

    val sendCrashLogs: Boolean
        get() = getBoolean(R.string.pref_send_crash_logs)

    val showDailyNotification: Boolean
        get() = getBoolean(R.string.pref_show_daily_notify)

    val logVehicleEvents: Boolean
        get() = getBoolean(R.string.pref_log_events)

    val lineupListFilter: FilterState
        get() {
            val pref = getSet(R.string.pref_filter_key_show)
            if(pref.isEmpty())
                return FilterState()
            return FilterState("", true, true,
                pref.contains("tanksShow"), pref.contains("planesShow"), pref.contains("helisShow"),
                pref.contains("lowLineupShow"), pref.contains("highLineupShow"), pref.contains("nowLineupShow"), pref.contains("laterLineupShow")
            )
        }

}