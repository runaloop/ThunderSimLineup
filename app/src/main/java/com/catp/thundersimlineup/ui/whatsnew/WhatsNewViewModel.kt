package com.catp.thundersimlineup.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.catp.thundersimlineup.data.db.entity.Change
import com.catp.thundersimlineup.ui.whatsnew.ChangesRequestInteractor
import javax.inject.Inject

class WhatsNewViewModel : ViewModel() {

    @Inject
    lateinit var changesRequestInteractor: ChangesRequestInteractor

    private val _changes = MutableLiveData<List<Change>>().apply {
        //
    }


    val changes: LiveData<List<Change>> = _changes

    fun viewCreated() {
        changesRequestInteractor.getChanges().subscribe {
            _changes.postValue(it)
        }
    }
}