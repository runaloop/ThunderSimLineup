package com.catp.thundersimlineup

import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import toothpick.InjectConstructor

inline fun <T> T?.whenNull(block: T?.() -> Unit): T? {
    if (this == null) block()
    return this@whenNull
}

inline fun <T> T?.whenNonNull(block: T.() -> Unit): T? {
    this?.block()
    return this@whenNonNull
}

fun <T> List<T>.lShift(n: Int) =
    let { slice(n % size until size) + slice(0 until n % size) }

fun <T> List<T>.rShift(n: Int) =
    let { lShift(size - n % size) }

@InjectConstructor
class LocalDateTimeProvider{
    fun now(): LocalDateTime = LocalDateTime.now(ZoneId.of("Z"))
}