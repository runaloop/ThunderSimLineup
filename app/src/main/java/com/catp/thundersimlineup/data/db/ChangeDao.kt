package com.catp.thundersimlineup.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.catp.thundersimlineup.data.db.entity.Change

@Dao
interface ChangeDao {

    @Query("SELECT * FROM CHANGE ORDER BY date")
    fun getChangeList(): List<Change>

    @Insert
    fun insertChanges(list: List<Change>)
}