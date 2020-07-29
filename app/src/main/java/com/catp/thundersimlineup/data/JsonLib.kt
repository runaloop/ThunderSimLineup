package com.catp.thundersimlineup.data

import com.dslplatform.json.DslJson
import com.dslplatform.json.runtime.Settings


object JsonLib {
    private var json: DslJson<Any>? = null
    fun get(): DslJson<Any> {
        var tmp = json
        if (tmp != null) return tmp
        //during initialization ServiceLoader.load should pick up services registered into META-INF/services
        //this doesn't really work on Android so DslJson will fallback to default generated class name
        //"dsl_json_Annotation_Processor_External_Serialization" and try to initialize it manually
        tmp = DslJson(Settings.basicSetup<Any>().includeServiceLoader().allowArrayFormat(true))
        //tmp = DslJson(DslJson.Settings<Any>().includeServiceLoader().allowArrayFormat(true))
        json = tmp
        return tmp
    }
}