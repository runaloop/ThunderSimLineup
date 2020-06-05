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
import javax.inject.Inject


@InjectConstructor
class LineupListViewModel(app: Application) : AndroidViewModel(app) {

    @Inject
    lateinit var lineupSchedule: Schedule

    @Inject
    lateinit var lineupStorage: LineupStorage

    private val _refreshResult = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    private val _selectedDay = MutableLiveData<CalendarDay>().apply { value = CalendarDay.today() }


    private val _lineupLoadStatus = MutableLiveData<Boolean>().apply { value = true }


    val text: LiveData<String> = _refreshResult
    val selectedDay: LiveData<CalendarDay> = _selectedDay
    val daySubject: PublishSubject<LocalDate> = PublishSubject.create()
    val currentLineup = MutableLiveData<Pair<Lineup?, Lineup?>>()
    val subscription = daySubject
        .doOnError { _lineupLoadStatus.postValue(false) }
        .observeOn(Schedulers.io())
        //.subscribeOn(Schedulers.io())
        .subscribe {
            _lineupLoadStatus.postValue(true)
            with(lineupSchedule) {
                try {
                    updateRule()
                    val low = lineupSchedule.getLineupForDate(it, LineupType.LOW)
                    val top = lineupSchedule.getLineupForDate(it, LineupType.TOP)
                    currentLineup.postValue(Pair(low, top))
                } catch (e: Exception) {
                    //TODO: correct error handling
                }
            }
            _lineupLoadStatus.postValue(false)
        }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    fun onDateChanged(date: CalendarDay) {
        _selectedDay.value = date
        daySubject.onNext(date.date)
    }

    fun refreshData(force: Boolean) {
        lineupStorage
            .refresh(this.getApplication(), force)
            .doOnSubscribe { _lineupLoadStatus.postValue(true) }
            .doOnError { _lineupLoadStatus.postValue(false) }
            .doOnComplete { _lineupLoadStatus.postValue(false) }
            .subscribe { result ->
                when (result) {
                    LineupStorage.REFRESH_RESULT.NEW_DATA -> _refreshResult.postValue("Lineups have been updated")
                    LineupStorage.REFRESH_RESULT.NO_NEW_DATA -> _refreshResult.postValue("Lineups loaded and ready to work")
                }
            }
    }

}