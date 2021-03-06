package com.catp.thundersimlineup.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import com.catp.thundersimlineup.MainActivityViewModel
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.annotation.ActivityViewModelScope
import kotlinx.android.synthetic.main.calendar_fragment.*
import toothpick.ktp.KTP
import javax.inject.Inject


class CalendarFragment : Fragment() {
    @Inject
    lateinit var currentDayDecorator: CurrentDayDecorator

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

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
        super.onViewCreated(view, savedInstanceState)
        calendarView.setDayFormatter { day -> day.day.toString() }
        calendarView.selectedDate = mainActivityViewModel.calendarDate.value
        calendarView.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                mainActivityViewModel.onDateChanged(date)
                mainActivityViewModel.pushFavorites()
            }
        }
        calendarView.addDecorators(currentDayDecorator)
    }

    @VisibleForTesting
    private fun injectDependencies() {
        KTP.openScopes(ApplicationScope::class.java)
            .openSubScope(ActivityViewModelScope::class.java)
            .inject(this)
    }


}
