package com.catp.thundersimlineup

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