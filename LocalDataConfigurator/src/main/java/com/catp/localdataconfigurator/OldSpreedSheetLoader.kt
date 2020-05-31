package com.catp.localdataconfigurator

import com.catp.model.LineupConfigurator

class OldSpreedSheetLoader {

    lateinit var lineupList: List<LineupConfigurator>

    companion object {
        const val FILENAME = "file:/./WT SB 1.93 FORUM LINEUPS.csv"
    }

    fun load() {
        val data = Loader().load(FILENAME, true)
        println("Parsing old spredsheet")
        lineupList = OldSpreedSheetParser().parse(data)
        println("Parsing done")
    }
}