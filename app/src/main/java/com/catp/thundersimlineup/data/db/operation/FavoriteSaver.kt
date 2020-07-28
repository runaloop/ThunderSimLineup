package com.catp.thundersimlineup.data.db.operation

import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.Vehicle
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class FavoriteSaver {

    @Inject
    lateinit var dao: LineupDao

    lateinit var favorites: List<Vehicle>

    fun save() {
        favorites = dao.getFavoriteVehicles()
    }

    fun restore() {
        if (favorites.isNotEmpty()) {
            val actual = dao.getVehicles()
                .filter { it.vehicleId in favorites.map { it.vehicleId } }
            actual.forEach { it.isFavorite = true }
            dao.insertVehicles(actual)
        }
    }
}