package com.catp.thundersimlineup.ui.vehiclelist

import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.Vehicle
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class VehicleRequestInteractor {
    @Inject
    lateinit var lineupDao: LineupDao

    fun getVehicles(): Observable<List<Vehicle>> {
        return Observable.just(lineupDao)
            .observeOn(Schedulers.io())
            .map { lineupDao.getVehicles()}
    }
}