package com.catp.thundersimlineup.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate

@Entity
data class Change(
    @PrimaryKey(autoGenerate = true) var changeId: Long = 0,
    val text: String,
    val date: LocalDate
) {
}