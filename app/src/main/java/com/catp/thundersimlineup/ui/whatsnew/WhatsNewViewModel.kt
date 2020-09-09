package com.catp.thundersimlineup.ui.whatsnew

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catp.thundersimlineup.data.db.entity.Change
import kotlinx.coroutines.launch
import javax.inject.Inject

class WhatsNewViewModel : ViewModel() {

    @Inject
    lateinit var changesRequestInteractor: ChangesRequestInteractor

    private val _changes = MutableLiveData<List<Change>>()

    val changes: LiveData<List<Change>> = _changes
    override fun onCleared() {
        super.onCleared()
    }

    fun viewCreated() {
        viewModelScope.launch {
            val result = changesRequestInteractor.getChanges()
            _changes.value = result
        }
    }
}