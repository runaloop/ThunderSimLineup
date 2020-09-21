package com.catp.thundersimlineup.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.catp.thundersimlineup.data.db.entity.Change
import kotlinx.coroutines.flow.Flow

@Dao
interface ChangeDao {

    @Query("SELECT * FROM CHANGE ORDER BY date")
    fun getChangeList(): Flow<List<Change>>

    @Insert
    fun insertChanges(list: List<Change>)
}