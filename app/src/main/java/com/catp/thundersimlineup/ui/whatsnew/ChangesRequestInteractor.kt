package com.catp.thundersimlineup.ui.whatsnew

import com.catp.thundersimlineup.data.db.ChangeDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class ChangesRequestInteractor {
    @Inject
    lateinit var changeDao: ChangeDao

    suspend fun getChanges() = withContext(Dispatchers.IO) {
        changeDao.getChangeList()
    }
}