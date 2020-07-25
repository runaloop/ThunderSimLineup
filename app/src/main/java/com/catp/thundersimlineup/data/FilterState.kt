package com.catp.thundersimlineup.data

data class FilterState(
    val text: String,
    var teamAShow: Boolean,
    var teamBShow: Boolean,
    var tanksShow: Boolean,
    var planesShow: Boolean,
    var helisShow: Boolean,
    var lowLineupShow: Boolean,
    var highLineupShow: Boolean,
    var nowLineupShow: Boolean,
    var laterLineupShow: Boolean
) {
    val data = listOf(
        teamAShow,
        teamBShow,
        tanksShow,
        planesShow,
        helisShow,
        lowLineupShow,
        highLineupShow,
        nowLineupShow,
        laterLineupShow
    )

    operator fun get(n: Int): Boolean = data[n]
}
