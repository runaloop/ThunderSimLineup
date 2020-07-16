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

    @Inject
    lateinit var lineupFilterByLineup: LineupFilterByLineup


    private val _refreshResult = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    private val _filterStatus = MutableLiveData<FilterState>().apply {
        value = FilterState(
            "",
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true
        )
    }
    private val _filterAvailable = MutableLiveData<FilterState>()
    private val _lineupLoadStatus = MutableLiveData<Boolean>().apply { value = true }

    val text: LiveData<String> = _refreshResult
    val daySubject: PublishSubject<LocalDate> = PublishSubject.create()
    private val _currentLineup = MutableLiveData<LineupRequestInteractor.LineupForToday>()
    val currentLineup: LiveData<LineupRequestInteractor.LineupForToday> = _currentLineup
    val filterStatus: LiveData<FilterState> = _filterStatus
    val filterAvailable: LiveData<FilterState> = _filterAvailable

    val subscription = daySubject
        .doOnError { _lineupLoadStatus.postValue(false) }
        .observeOn(Schedulers.io())
        //.subscribeOn(Schedulers.io())
        .subscribe { day ->
            _lineupLoadStatus.postValue(true)
            val lineupForToday = lineupRequestInteractor.getLineupForADay(day)
            val lineupAvailableFilters = lineupFilterByLineup.getFilters(lineupForToday)
            _filterAvailable.postValue(lineupAvailableFilters)
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
        highLineupShow: Boolean,
        nowLineupShow: Boolean,
        laterLineupShow: Boolean
    ) {
        _filterStatus.value = FilterState(
            filterText,
            teamAShow,
            teamBShow,
            tanksShow,
            planesShow,
            helisShow,
            lowLineupShow,
            highLineupShow,
            nowLineupShow,
            laterLineupShow
        )
    }

    fun onDateChanged(date: CalendarDay) {
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

    data class FilterState(
        val text: String,
        var teamAShow: Boolean,
        var teamBShow: Boolean,
        var tanksShow: Boolean,
        var planesShow: Boolean,
        var helisShow: Boolean,
        var lowLineupShow: Boolean,
        var highLineupShow: Boolean,
        var nowLineupShow: Boolean,
        var laterLineupShow: Boolean
    ) {
        val data = listOf(
            teamAShow,
            teamBShow,
            tanksShow,
            planesShow,
            helisShow,
            lowLineupShow,
            highLineupShow,
            nowLineupShow,
            laterLineupShow
        )

        operator fun get(n: Int): Boolean = data[n]
    }
}