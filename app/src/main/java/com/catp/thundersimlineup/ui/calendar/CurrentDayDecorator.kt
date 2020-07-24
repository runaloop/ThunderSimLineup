package com.catp.thundersimlineup.ui.calendar

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.catp.thundersimlineup.LocalDateProvider
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.whenNull
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import toothpick.InjectConstructor
import javax.inject.Inject


@InjectConstructor
class CurrentDayDecorator : DayViewDecorator {

    @Inject
    lateinit var dateProvider: LocalDateProvider

    @Inject
    lateinit var context: Application

    var drawable: Drawable? = null

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return CalendarDay.from(dateProvider.now()) == day
    }

    override fun decorate(view: DayViewFacade) {
        drawable.whenNull {
            drawable = ContextCompat.getDrawable(context, R.drawable.today_decorator_bg)
        }

        view.setBackgroundDrawable(drawable!!)
    }
}

