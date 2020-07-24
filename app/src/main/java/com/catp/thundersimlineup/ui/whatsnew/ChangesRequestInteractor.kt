package com.catp.thundersimlineup.ui.whatsnew

import com.catp.thundersimlineup.data.db.ChangeDao
import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.Change
import com.catp.thundersimlineup.data.db.entity.Vehicle
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class ChangesRequestInteractor {
    @Inject
    lateinit var changeDao: ChangeDao

    fun getChanges(): Observable<List<Change>> {
        return Observable.just(changeDao)
            .observeOn(Schedulers.io())
            .map { changeDao.getChangeList()}
    }
}