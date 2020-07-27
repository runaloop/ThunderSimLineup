package com.catp.thundersimlineup.ui.preferences

import android.os.Bundle
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.StatUtil
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.annotation.ViewModelScope
import com.catp.thundersimlineup.ui.lineuplist.LineupListViewModel
import com.google.android.material.snackbar.Snackbar
import toothpick.ktp.KTP
import javax.inject.Inject


class PreferencesFragment : PreferenceFragmentCompat() {
    @Inject
    lateinit var statUtil: StatUtil

    @Inject
    lateinit var lineupListViewModel: LineupListViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectDependencies()
        statUtil.sendViewStat(this, "Prefs")
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference?.key == requireContext().getString(R.string.pref_refresh_lineup_list)) {
            Snackbar.make(requireView(), getString(R.string.refresh_started), Snackbar.LENGTH_SHORT)
                .show()
            lineupListViewModel.refreshData(true)
        }
        return super.onPreferenceTreeClick(preference)
    }

    @VisibleForTesting
    private fun injectDependencies() {
        KTP.openScopes(ApplicationScope::class.java)
            .openSubScope(ViewModelScope::class.java)
            .inject(this)
    }


}
