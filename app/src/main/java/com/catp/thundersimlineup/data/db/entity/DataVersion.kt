package com.catp.thundersimlineup.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DataVersion(val version: Int, @PrimaryKey val id: Long = 1)