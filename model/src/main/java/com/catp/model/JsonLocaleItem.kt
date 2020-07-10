package com.catp.model

import com.dslplatform.json.CompiledJson

@CompiledJson(formats = [CompiledJson.Format.ARRAY])
data class JsonLocaleItem(
    val id: String,
    var title: String,
    var nation: String = "UNKNOWN"
) {


    fun exactMatch(name: String): Boolean {

        var convertedName = name
        listOf("0", "shop").forEach { ending ->
            if (name.endsWith("_$ending"))
                convertedName = name.replace("_$ending", "")
        }

        return listOf(
            id,
            title
        ).any {
            convertedName == it
        }
    }

    fun similarMatch(name: String) =
        listOf( title ).any {
            it.contains(name)
        }

}