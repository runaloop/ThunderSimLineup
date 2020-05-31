package com.catp.thundersimlineup.data.db.operation

import com.catp.model.JsonTeam
import com.catp.thundersimlineup.data.db.entity.TeamWithVehicleCrossRef
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class FindVehiclesToDeleteTest {

    val findVehiclesToDelete = FindVehiclesToDelete()
    @Before
    fun setUp() {
    }

    @Test
    fun `Empty db, and filled json, do nothing`() {
        //GIVEN
        val jsonTeam = JsonTeam(mutableListOf("1", "2", "3"))
        val crossRefList = emptyList<TeamWithVehicleCrossRef>()

        //WHEN
        val result = findVehiclesToDelete.process(jsonTeam, crossRefList)

        //THEN
        assertThat(result).isEmpty()
    }

    @Test
    fun `Filled db, and filled json, data is the same, do  nothing`() {
        //GIVEN
        val jsonTeam = JsonTeam(mutableListOf("1", "2", "3"))
        val crossRefList = listOf<TeamWithVehicleCrossRef>(
            TeamWithVehicleCrossRef(1, "1"),
            TeamWithVehicleCrossRef(1, "2"),
            TeamWithVehicleCrossRef(1, "3")
        )

        //WHEN
        val result = findVehiclesToDelete.process(jsonTeam, crossRefList)

        //THEN
        assertThat(result).isEmpty()
    }

    @Test
    fun `Filled db, and json has less data, mark vehicles as deleted and return`() {
        //GIVEN
        val jsonTeam = JsonTeam(mutableListOf("2", "3"))
        val toDelete = TeamWithVehicleCrossRef(1, "1")
        val crossRefList = listOf<TeamWithVehicleCrossRef>(
            toDelete,
            TeamWithVehicleCrossRef(1, "2"),
            TeamWithVehicleCrossRef(1, "3")
        )

        //WHEN
        val result = findVehiclesToDelete.process(jsonTeam, crossRefList)

        //THEN
        assertThat(result).containsExactly(toDelete)
    }


}