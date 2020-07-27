package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonTeam
import com.catp.thundersimlineup.data.db.entity.TeamWithVehicleCrossRef
import com.catp.thundersimlineup.data.db.entity.VehicleStatus
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class FindNewVehiclesTest {

    private val findNewVehicles = FindNewVehicles()

    @Before
    fun setUp() {
    }

    @Test
    fun `db has no data, return filled list`() {
        //GIVEN
        val jsonTeam = JsonTeam(mutableListOf("1", "2"))
        val localData = emptyList<TeamWithVehicleCrossRef>()
        val teamId = 323231L
        val lineupId = "1_1"

        //WHEN
        val result = findNewVehicles.process(jsonTeam, localData, teamId, lineupId)

        //THEN
        assertThat(result).containsExactly(
            TeamWithVehicleCrossRef(teamId, "1", VehicleStatus.NEW),
            TeamWithVehicleCrossRef(teamId, "2", VehicleStatus.NEW)
        )
    }

    @Test
    fun `db has the same value as json, return empty list`() {
        //GIVEN
        val jsonTeam = JsonTeam(mutableListOf("1", "2"))
        val teamId = 323231L
        val localData = listOf(
            TeamWithVehicleCrossRef(teamId, "1", VehicleStatus.REGULAR),
            TeamWithVehicleCrossRef(teamId, "2", VehicleStatus.REGULAR)
        )
        val lineupId = "1_1"

        //WHEN
        val result = findNewVehicles.process(jsonTeam, localData, teamId, lineupId)

        //THEN
        assertThat(result).isEmpty()
    }


    @Test
    fun `db has few data, but json has the same and one more, return new item`() {
        //GIVEN
        val jsonTeam = JsonTeam(mutableListOf("1", "2", "3"))
        val teamId = 323231L
        val localData = listOf(
            TeamWithVehicleCrossRef(teamId, "1", VehicleStatus.REGULAR),
            TeamWithVehicleCrossRef(teamId, "2", VehicleStatus.REGULAR)
        )
        val lineupId = "1_1"

        //WHEN
        val result = findNewVehicles.process(jsonTeam, localData, teamId, lineupId)

        //THEN
        assertThat(result).containsExactly(TeamWithVehicleCrossRef(teamId, "3", VehicleStatus.NEW))
    }

}