package com.catp.thundersimlineup.data.db

import androidx.room.*
import com.catp.thundersimlineup.data.db.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LineupDao {


    @Query("SELECT * FROM LineupEntity")
    @Transaction
    fun getLineups(): List<Lineup>

    @Query("SELECT * FROM LineupEntity")
    fun getLineupsEntity(): List<LineupEntity>

    @Insert
    fun insertTeam(team: TeamEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVehicle(vehicle: Vehicle): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    fun insertVehicles(vehicles: List<Vehicle>): List<Long>

    @Update
    @Transaction
    fun updateVehicles(vehicles: List<Vehicle>): Int

    @Insert
    @Transaction
    fun insertTeams(teams: List<TeamEntity>): List<Long>

    @Insert
    @Transaction
    fun insertLineups(lineups: List<LineupEntity>): List<Long>


    @Insert
    @Transaction
    fun insertTeamWithVehicleCrossRef(teamWithVehicle: List<TeamWithVehicleCrossRef>): List<Long>

    @Query("SELECT * FROM TeamWithVehicleCrossRef")
    fun getTeamWithVehicleCrossRef(): List<TeamWithVehicleCrossRef>

    @Delete
    fun deleteVehicleCrossRef(list: List<TeamWithVehicleCrossRef>)

    @Update
    fun updateVehicleCrossRef(list: List<TeamWithVehicleCrossRef>)

    @Query("SELECT * FROM TeamEntity")
    fun getTeamTable(): List<TeamEntity>

    @Transaction
    fun setVersion(version: Int): Long {
        return upsertVersion(
            DataVersion(
                version = version
            )
        )
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertVersion(version: DataVersion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateVersion(version: DataVersion): Long

    @Transaction
    fun upsertVersion(version: DataVersion): Long {
        val result = insertVersion(version)
        return if (result == -1L)
            updateVersion(version)
        else result
    }

    @Query("SELECT * FROM DataVersion LIMIT 1")
    fun getVersion(): DataVersion?


    @Query("SELECT * FROM Vehicle")
    fun getVehiclesFlow(): Flow<List<Vehicle>>

    @Query("SELECT * FROM Vehicle")
    fun getVehicles(): List<Vehicle>

    @Query("SELECT * FROM Vehicle WHERE isFavorite==1")
    fun getFavoriteVehicles(): List<Vehicle>

    @Query("SELECT * FROM LineupCycleEntity ORDER BY orderNumber")
    fun getLineupCycleList(): List<LineupCycleEntity>

    @Query("SELECT * FROM LineupToBREntity")
    fun getLineupToBr(): List<LineupToBREntity>

    @Query("SELECT * FROM LineupShiftEntity")
    fun getLineupShift(): List<LineupShiftEntity>

    @Query("SELECT * FROM LineupCycleAvailabilityEntity")
    fun getLineupAvailability(): LineupCycleAvailabilityEntity?

    @Update
    fun updateLineupCycleList(data: List<LineupCycleEntity>)

    @Insert
    fun insertLineupCycleList(data: List<LineupCycleEntity>)

    @Query("DELETE FROM LineupCycleEntity")
    fun deleteLineupCycleList()

    @Query("DELETE FROM LineupCycleAvailabilityEntity")
    fun deleteLineupAvailability()

    @Query("DELETE FROM LineupToBREntity")
    fun deleteLineupToBr()

    @Query("DELETE FROM LineupShiftEntity")
    fun deleteLineupShift()

    @Transaction
    @Insert
    fun insertLineupToBREntity(data: List<LineupToBREntity>)

    @Transaction
    @Insert
    fun insertLineupCycleAvailability(data: List<LineupCycleAvailabilityEntity>)

    @Transaction
    @Insert
    fun insertLineupCycleShift(data: List<LineupShiftEntity>)

}