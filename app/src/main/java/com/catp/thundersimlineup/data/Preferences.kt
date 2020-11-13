package com.catp.thundersimlineup.data

import android.app.Application
import androidx.preference.PreferenceManager
import com.catp.thundersimlineup.R
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class Preferences {

    @Inject
    lateinit var context: Application

    private val preferenceManager by lazy {
        PreferenceManager
            .getDefaultSharedPreferences(context)
    }

    private fun getBoolean(res: Int, defaultValue: Boolean = true): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(
                context.resources.getString(res), defaultValue
            )
    }

    private fun getSet(res: Int): MutableSet<String> {
        return preferenceManager.getStringSet(
                context.resources.getString(res), emptySet()
            )!!
    }

    val collapseLineupListItems: Boolean
        get() = getBoolean(R.string.pref_collapse_lineup_list_items)
    val sendStat: Boolean
        get() = getBoolean(R.string.pref_send_statistics)
    val sendCrashLogs: Boolean
        get() = getBoolean(R.string.pref_send_crash_logs)

    val showDailyNotification: Boolean
        get() = getBoolean(R.string.pref_show_daily_notify)

    val logVehicleEvents: Boolean
        get() = getBoolean(R.string.pref_log_events)

    val lineupListFilter: FilterState
        get() {
            val pref = getSet(R.string.pref_filter_key_show)
            if (pref.isEmpty())
                return FilterState()
            return FilterState(
                "",
                teamAShow = true,
                teamBShow = true,
                tanksShow = pref.contains("tanksShow"),
                planesShow = pref.contains("planesShow"),
                helisShow = pref.contains("helisShow"),
                lowLineupShow = pref.contains("lowLineupShow"),
                highLineupShow = pref.contains("highLineupShow"),
                nowLineupShow = pref.contains("nowLineupShow"),
                laterLineupShow = pref.contains("laterLineupShow")
            )
        }

}