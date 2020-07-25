package com.catp.thundersimlineup.ui.lineuplist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.catp.thundersimlineup.data.FilterState
import com.catp.thundersimlineup.data.LineupStorage
import com.catp.thundersimlineup.data.Schedule
import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.prolificinteractive.materialcalendarview.CalendarDay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.threeten.bp.LocalDate
import toothpick.InjectConstructor
import java.util.concurrent.atomic.AtomicBoolean
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

    @Inject
    lateinit var pushFavoriteVehicleInteractor: PushFavoriteVehicleInteractor

    val selectedItems = mutableSetOf<Vehicle>()


    private val _refreshResult = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    private val _filterStatus = MutableLiveData<FilterState>().apply {
        value = FilterState()
    }
    private val _filterAvailable = MutableLiveData<FilterState>()
    private val _lineupLoadStatus = MutableLiveData<Boolean>()

    val text: LiveData<String> = _refreshResult
    val daySubject: PublishSubject<LocalDate> = PublishSubject.create<LocalDate>()
    private val _currentLineup = MutableLiveData<LineupRequestInteractor.LineupForToday>()
    val currentLineup: LiveData<LineupRequestInteractor.LineupForToday> = _currentLineup
    val filterStatus: LiveData<FilterState> = _filterStatus
    val filterAvailable: LiveData<FilterState> = _filterAvailable
    val lineupLoadStatus: LiveData<Boolean> = _lineupLoadStatus
    val dbUpdates = AtomicBoolean()

    val cs = CompositeDisposable()
    val subscribtion = daySubject
        .doOnError {
            _lineupLoadStatus.postValue(false)
        }
        .observeOn(Schedulers.io())
        //.subscribeOn(Schedulers.io())
        .subscribe { day ->
            if (!dbUpdates.get()) {
                val lineupForToday = lineupRequestInteractor.getLineupForADay(day)
                val lineupAvailableFilters = lineupFilterByLineup.getFilters(lineupForToday)
                _filterAvailable.postValue(lineupAvailableFilters)
                _currentLineup.postValue(lineupForToday)
                _lineupLoadStatus.postValue(false)
            } else {
            }
        }.apply { cs.add(this) }

    override fun onCleared() {
        super.onCleared()
        cs.dispose()
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

    fun onDateChanged(date: CalendarDay, updateLoadingStatus: Boolean = true) {
        if (updateLoadingStatus)
            _lineupLoadStatus.value = true
        pushFavorites()
        daySubject.onNext(date.date)
    }

    fun refreshData(force: Boolean) {
        startDBRefresh()
        cs.add(lineupStorage
            .refresh(this.getApplication(), force)
            .doOnError {
                refreshDBError()
            }
            .map { result ->
                result
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                when (result) {
                    LineupStorage.REFRESH_RESULT.NEW_DATA -> _refreshResult.value =
                        "Lineups have been updated"
                    LineupStorage.REFRESH_RESULT.NO_NEW_DATA -> _refreshResult.value =
                        "Lineups loaded and ready to work"
                }
                refreshDBFinished()
            })
    }

    private fun startDBRefresh() {
        dbUpdates.set(true)
        _lineupLoadStatus.value = true
    }

    private fun refreshDBError() {
        _lineupLoadStatus.postValue(false)
        dbUpdates.set(false)
    }

    fun refreshDBFinished() {
        dbUpdates.set(false)
        onDateChanged(CalendarDay.from(LocalDate.now()), false)
    }

    fun favoriteUpdated() {

    }

    fun onClick(vehicle: Vehicle) {
        vehicle.isFavorite = !vehicle.isFavorite
        selectedItems += vehicle
    }

    fun pushFavorites() {
        if (selectedItems.isNotEmpty()) {
            val items = selectedItems.toList()
            selectedItems.clear()
            pushFavoriteVehicleInteractor.push(items).subscribe {
                favoriteUpdated()
            }
        }
    }

}