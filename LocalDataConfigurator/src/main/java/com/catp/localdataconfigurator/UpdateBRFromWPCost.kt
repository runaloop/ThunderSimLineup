package com.catp.localdataconfigurator

import com.catp.model.JsonLineup
import com.catp.model.JsonRules
import com.catp.model.JsonVehicleStore
import com.catp.model.VehicleType

class UpdateBRFromWPCost(
    val lineups: List<JsonLineup>,
    private val wpCost: WPCost,
    private val vehicleStore: JsonVehicleStore
) {
    fun process() {
        val lineupToBR = JsonRules().LINEUP_TO_BR_RELATION
        vehicleStore.vehicleList.forEach { vehicle ->
            wpCost.vehicleItems[vehicle.name]?.let { vehicleItem ->
                if (vehicle.BR != vehicleItem.br) {
                    if(vehicleItem.unitClass == VehicleType.PLANE){
                        val lineupsBefore = lineupToBR.filter { pair -> pair.value.contains(vehicle.BR) }.keys
                        val lineupsAfter = lineupToBR.filter { pair -> pair.value.contains(vehicleItem.br) }.keys
                        if(lineupsAfter.isNotEmpty())
                            println("${vehicle.name} изменения БР ${vehicle.BR}-${vehicleItem.br} лайнапы до: $lineupsBefore лайнапы после: $lineupsAfter")
                    }
                    vehicle.BR = vehicleItem.br
                }
            }
        }
    }
}
