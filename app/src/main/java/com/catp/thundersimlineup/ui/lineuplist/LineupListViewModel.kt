package com.catp.thundersimlineup.ui.lineuplist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.catp.thundersimlineup.data.LineupStorage
import com.catp.thundersimlineup.data.Schedule
import com.prolificinteractive.materialcalendarview.CalendarDay
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.threeten.bp.LocalDate
import toothpick.InjectConstructor
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@InjectConstructor
class LineupListViewModel(app: Application) : AndroidViewModel(app) {

    @Inject
    lateinit var lineupSchedule: Schedule

    @Inject
    lateinit var lineupStorage: LineupStorage

    @Inject
    lateinit var lineupRequestInteractor: LineupRequestInteractor


    private val _refreshResult = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    private val _selectedDay = MutableLiveData<CalendarDay>().apply { value = CalendarDay.today() }

    private val _filterStatus = MutableLiveData<FilterState>().apply {
        value = FilterState("", true, true, true, true, true, true, true)
    }
    private val _lineupLoadStatus = MutableLiveData<Boolean>().apply { value = true }

    val text: LiveData<String> = _refreshResult
    val selectedDay: LiveData<CalendarDay> = _selectedDay
    val daySubject: PublishSubject<LocalDate> = PublishSubject.create()
    private val _currentLineup = MutableLiveData<LineupRequestInteractor.LineupForToday>()
    val currentLineup: LiveData<LineupRequestInteractor.LineupForToday> = _currentLineup
    val filterStatus: LiveData<FilterState> = _filterStatus

    val subscription = daySubject
        .doOnError { _lineupLoadStatus.postValue(false) }
        .observeOn(Schedulers.io())
        //.subscribeOn(Schedulers.io())
        .subscribe { day ->
            _lineupLoadStatus.postValue(true)
            val lineupForToday = lineupRequestInteractor.getLineupForADay(day)
            _currentLineup.postValue(lineupForToday)
            _lineupLoadStatus.postValue(false)
        }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    fun filterChange(
        filterText: String,
        teamAShow: Boolean,
        teamBShow: Boolean,
        tanksShow: Boolean,
        planesShow: Boolean,
        helisShow: Boolean,
        lowLineupShow: Boolean,
        highLineupShow: Boolean
    ) {
        _filterStatus.value = FilterState(
            filterText,
            teamAShow,
            teamBShow,
            tanksShow,
            planesShow,
            helisShow,
            lowLineupShow,
            highLineupShow
        )
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
                /*lineupStorage.lineupDao.getVehicles().find { it.vehicleId == "hawk_iii" }.whenNonNull {
                    isFavorite = true
                    lineupStorage.lineupDao.updateVehicles(listOf(this))
                }*/

            }
    }

    data class FilterState(
        val text: String,
        val teamAShow: Boolean,
        val teamBShow: Boolean,
        val tanksShow: Boolean,
        val planesShow: Boolean,
        val helisShow: Boolean,
        val lowLineupShow: Boolean,
        val highLineupShow: Boolean

    )
}