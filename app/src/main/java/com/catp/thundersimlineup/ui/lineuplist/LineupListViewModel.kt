package com.catp.thundersimlineup.ui.lineuplist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.catp.thundersimlineup.data.LineupStorage
import com.catp.thundersimlineup.data.Schedule
import com.catp.thundersimlineup.data.db.entity.Lineup
import com.catp.thundersimlineup.data.db.entity.LineupType
import com.prolificinteractive.materialcalendarview.CalendarDay
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.threeten.bp.LocalDate
import toothpick.InjectConstructor
import toothpick.ktp.delegate.inject


@InjectConstructor
class LineupListViewModel(app: Application) : AndroidViewModel(app) {

    private val lineupStorage by inject<LineupStorage>()
    private val lineupSchedule: Schedule by inject<Schedule>()
    private val _refreshResult = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    private val _selectedDay = MutableLiveData<CalendarDay>().apply { value = CalendarDay.today() }


    private val _lineupLoadStatus = MutableLiveData<Boolean>().apply { value = true }

    /*private val _onViewModelStart = MutableLiveData<Lineup>().apply {
        lineupStorage
            .refresh(app, false)
            .doOnSubscribe { _lineupLoadStatus.postValue(true) }
            .doOnError { _lineupLoadStatus.postValue(false) }
            .subscribe { result ->
                when (result) {
                    LineupStorage.REFRESH_RESULT.NEW_DATA -> _refreshResult.postValue("Lineups have been updated")
                    LineupStorage.REFRESH_RESULT.NO_NEW_DATA -> _refreshResult.postValue("")
                }
            }
    }*/

    val text: LiveData<String> = _refreshResult
    val selectedDay: LiveData<CalendarDay> = _selectedDay
    val daySubject: PublishSubject<LocalDate> = PublishSubject.create()
    val currentLineup = MutableLiveData<Pair<Lineup?, Lineup?>>()
    val subscription = daySubject
        .doOnSubscribe { _lineupLoadStatus.postValue(true) }
        .doOnComplete { _lineupLoadStatus.postValue(false) }
        .doOnError { _lineupLoadStatus.postValue(false) }
        .observeOn(Schedulers.io())
        .subscribe {
            lineupSchedule.updateRule()
            val low = lineupSchedule.getLineupForDate(it, LineupType.LOW)
            val top = lineupSchedule.getLineupForDate(it, LineupType.TOP)
            currentLineup.postValue(Pair(low, top))
        }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    fun onDateChanged(date: CalendarDay) {
        _selectedDay.value = date
        daySubject.onNext(date.date)
    }

}