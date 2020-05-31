package com.catp.model

import com.dslplatform.json.CompiledJson

@CompiledJson(formats = [CompiledJson.Format.ARRAY])
data class JsonLocaleItem(
    val id: String,
    var englishTitle: String,
    var russianTitle: String,
    var fullEnglishTitle: String,
    var fullRussianTitle: String,
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
            fullEnglishTitle,
            fullRussianTitle,
            englishTitle,
            russianTitle
        ).any {
            convertedName == it
        }
    }

    fun similarMatch(name: String) =
        listOf(englishTitle, russianTitle, fullEnglishTitle, fullRussianTitle).any {
            it.contains(name)
        }

}