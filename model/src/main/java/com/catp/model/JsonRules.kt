package com.catp.model

import com.dslplatform.json.CompiledJson

//Update version field on a any json generation
@CompiledJson
class JsonRules(
    val lineupCycleRule1: List<String> = LINEUP_SEQUEQNCE_LOW,
    val lineupCycleRule2: List<String> = LINEUP_SEQUEQNCE_TOP,
    val lineupCycleRuleExcremental: List<String> = emptyList(),
    val lineupShiftRule1: Map<String, String> = mapOf("2020-06-10" to "4_1"),
    val lineupShiftRule2: Map<String, String> = mapOf("2020-06-10" to "9_2"),
    val lineupAvailability: Map<String, Pair<String, String>> = mapOf(
        /*"E_1" to Pair(
            "2019-12-26",
            "2019-12-29"
        )*/
    ),
    val LINEUP_TO_BR_RELATION: Map<String, List<String>> = mapOf(
        "1_1" to listOf("1.0", "1.3", "1.7", "2.0"), // actual
        "2_1" to listOf("1.7", "2.0", "2.3", "2.7"),
        "3_1" to listOf("2.3", "2.7", "3.0", "3.3"),
        "4_1" to listOf("3.0", "3.3", "3.7", "4.0"),
        "5_1" to listOf("4.0", "4.3", "4.7", "5.0"),
        "6_1" to listOf("5.0", "5.3", "5.7", "6.0", "6.3")
    )
) {
    companion object {
        val LINEUP_SEQUEQNCE_LOW = listOf("1_1", "3_1", "2_1", "5_1", "4_1", "6_1")
        val LINEUP_SEQUEQNCE_TOP = listOf("8_2", "10_2", "8_2_2", "9_2")
        val LINEUP_UTC_TIME_OF_CHANGE = 11
        val VERSION = 3
    }


    override fun toString(): String {
        return "Rules(lineupCycleRule1=$lineupCycleRule1, lineupCycleRule2=$lineupCycleRule2, lineupShiftRule1=$lineupShiftRule1, lineupShiftRule2=$lineupShiftRule2)"
    }


}
