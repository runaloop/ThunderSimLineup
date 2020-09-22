package com.catp.thundersimlineup.ui.lineuplist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.catp.thundersimlineup.data.FilterState
import com.catp.thundersimlineup.data.LineupStorage
import com.catp.thundersimlineup.data.Schedule
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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


    private val _refreshResult = MutableLiveData<String>()
    private val _filterStatus = MutableLiveData<FilterState>().apply {
        value = FilterState()
    }
    private val _filterAvailable = MutableLiveData<FilterState>()
    private val _lineupLoadStatus = MutableLiveData<Boolean>()

    val text: LiveData<String> = _refreshResult
    private val daySubject = Channel<LocalDate>()
    private val _currentLineup = MutableLiveData<LineupRequestInteractor.LineupForToday>()
    val currentLineup: LiveData<LineupRequestInteractor.LineupForToday> = _currentLineup
    val filterStatus: LiveData<FilterState> = _filterStatus
    val filterAvailable: LiveData<FilterState> = _filterAvailable
    val lineupLoadStatus: LiveData<Boolean> = _lineupLoadStatus
    private val dbUpdates = AtomicBoolean()
    private var selectedDay = LocalDate.now()

    init {
        viewModelScope.launch {
            daySubject
                .receiveAsFlow()
                .catch { _lineupLoadStatus.postValue(false) }
                .collectLatest { day ->
                    selectedDay = day
                    withContext(Dispatchers.IO) {
                        if (!dbUpdates.get()) {
                            val lineupForToday = lineupRequestInteractor.getLineupForADay(day)
                            val lineupAvailableFilters =
                                lineupFilterByLineup.getFilters(lineupForToday)
                            withContext(Dispatchers.Main) {
                                _filterAvailable.value = lineupAvailableFilters
                                _currentLineup.value = lineupForToday
                                _lineupLoadStatus.value = false
                            }
                        }
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
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
        viewModelScope.launch(Dispatchers.IO) {
            daySubject.send(date.date)
        }
    }

    fun refreshDataShowed() {
        _refreshResult.value = ""
    }

    fun refreshData(force: Boolean) {
        startDBRefresh()
        viewModelScope.launch {
            try {
                val result = lineupStorage.refresh(getApplication(), force)
                when (result) {
                    LineupStorage.RefreshResult.NEW_DATA -> _refreshResult.value =
                        "Lineups have been updated"
                    LineupStorage.RefreshResult.NO_NEW_DATA ->
                        _refreshResult.value = ""
                }
            } catch (exception: Exception) {
                refreshDBError()
            } finally {
                refreshDBFinished()
            }
        }
    }

    private fun startDBRefresh() {
        dbUpdates.set(true)
        _lineupLoadStatus.value = true
    }

    private fun refreshDBError() {
        _lineupLoadStatus.postValue(false)
        dbUpdates.set(false)
    }

    private fun refreshDBFinished() {
        dbUpdates.set(false)
        onDateChanged(CalendarDay.from(selectedDay), false)
    }

    private fun favoriteUpdated() {

    }


}