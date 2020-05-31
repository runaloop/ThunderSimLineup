package com.catp.thundersimlineup.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.annotation.ViewModelScope
import com.catp.thundersimlineup.ui.lineuplist.LineupListViewModel
import kotlinx.android.synthetic.main.calendar_fragment.*
import org.threeten.bp.LocalDate
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject


class CalendarFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val lineupListViewModel by inject<LineupListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.calendar_fragment, container, false)
        injectDependencies()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        calendarView.setDayFormatter { day -> day.day.toString() }
        calendarView.setSelectedDate(LocalDate.now())
        calendarView.setOnDateChangedListener { widget, date, selected ->
            if (selected) {
                lineupListViewModel.onDateChanged(date)
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    @VisibleForTesting
    private fun injectDependencies() {
        KTP.openScopes(ApplicationScope::class.java)
            .openSubScope(ViewModelScope::class.java)
            .inject(this)
    }


}
