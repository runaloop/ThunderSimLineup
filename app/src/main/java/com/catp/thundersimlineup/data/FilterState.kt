package com.catp.thundersimlineup.data

data class FilterState(
    val text: String = "",
    var teamAShow: Boolean = true,
    var teamBShow: Boolean = true,
    var tanksShow: Boolean = true,
    var planesShow: Boolean = true,
    var helisShow: Boolean = true,
    var lowLineupShow: Boolean = true,
    var highLineupShow: Boolean = true,
    var nowLineupShow: Boolean = true,
    var laterLineupShow: Boolean = true
) {
    val data: List<Boolean>
        get() {
            return listOf(
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
        }

    operator fun get(n: Int): Boolean = data[n]
}
