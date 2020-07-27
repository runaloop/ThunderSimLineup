package com.catp.localdataconfigurator

import com.catp.model.JsonLineup
import com.catp.model.JsonVehicleStore

class UpdateBRFromWPCost(
    val lineups: List<JsonLineup>,
    private val wpCost: WPCost,
    private val vehicleStore: JsonVehicleStore
) {
    fun process() {
        vehicleStore.vehicleList.forEach { vehicle ->
            wpCost.vehicleItems[vehicle.name]?.let {
                if (vehicle.br != it.br) {
                    println("${vehicle.name} updating br to ${vehicle.br}-${it.br}")
                    vehicle.br = it.br
                }
            }
        }
    }
}
