package com.mithrai.crowdai.support.utility

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun String.jsonToMap(): Map<String, String>? {
    var map = mapOf<String, String>()
    if (this.isNotEmpty()) {
        map = Gson().fromJson(
            this, object : TypeToken<Map<String?, String?>?>() {}.type
        )
    }
    return map
}

fun String.toDelimeterizedSet(): HashSet<String> {
    return if (!this.isEmpty()) {
        this.split(",").toHashSet()
    } else {
        hashSetOf()
    }
}