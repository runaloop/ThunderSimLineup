package com.catp.thundersimlineup.ui.lineuplist

import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.Vehicle
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class PushFavoriteVehicleInteractor {
    @Inject
    lateinit var lineupDao: LineupDao

    fun push(selectedItems: List<Vehicle>){
        lineupDao.updateVehicles(selectedItems)
    }
}