package com.catp.thundersimlineup.data.db.entity.converter

import androidx.room.TypeConverter
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

class InstantConverter {

    @TypeConverter
    fun toInstant(date: Long): Instant {
        return Instant.ofEpochMilli(date);
    }

    @TypeConverter
    fun toString(date: Instant): Long {
        return date.toEpochMilli()
    }

    @TypeConverter
    fun toLocalDate(date: Long): LocalDate {
        return LocalDateTime.ofInstant(toInstant(date), ZoneOffset.UTC).toLocalDate()
    }

    @TypeConverter
    fun localDateToString(date: LocalDate): Long {
        return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
    /*@TypeConverter
    fun toInstantStatus(date: Date): Instant {
        return Instant.ofEpochMilli(date.time);
    }

    @TypeConverter
    fun toString(date: Instant): Date {
        return Date(date.toEpochMilli());
    }*/
}