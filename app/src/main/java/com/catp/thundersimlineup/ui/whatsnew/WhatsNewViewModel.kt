package com.catp.thundersimlineup.ui.whatsnew

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.catp.thundersimlineup.data.db.entity.Change
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class WhatsNewViewModel : ViewModel() {

    @Inject
    lateinit var changesRequestInteractor: ChangesRequestInteractor

    private val _changes = MutableLiveData<List<Change>>().apply {
        //
    }


    val changes: LiveData<List<Change>> = _changes

    private val cs = CompositeDisposable()

    override fun onCleared() {
        cs.clear()
        super.onCleared()
    }

    fun viewCreated() {
        changesRequestInteractor.getChanges().subscribe {
            _changes.postValue(it)
        }.addTo(cs)
    }
}