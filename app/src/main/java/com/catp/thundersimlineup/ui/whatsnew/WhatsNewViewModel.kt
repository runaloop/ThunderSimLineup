package com.catp.thundersimlineup.ui.whatsnew

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catp.thundersimlineup.data.db.ChangeDao
import com.catp.thundersimlineup.data.db.entity.Change
import com.catp.thundersimlineup.ui.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class WhatsNewViewModel : BaseViewModel() {

    @Inject
    lateinit var changesDao: ChangeDao

    private val _changes = MutableLiveData<List<Change>>()
    val changes: LiveData<List<Change>> = _changes

    override fun onCreateAfterInject() {
        viewModelScope.launch {
            changesDao.getChangeList().collectLatest { _changes.value = it }
        }
    }
}