package com.support.dateClass

import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonDeserializationContext
import org.joda.time.format.ISODateTimeFormat
import com.google.gson.JsonSerializer
import com.google.gson.JsonDeserializer
import org.joda.time.LocalTime
import java.lang.reflect.Type

class LocalTimeSerializer : JsonDeserializer<LocalTime>, JsonSerializer<LocalTime> {

    @Throws(JsonParseException::class)
    override fun deserialize(
        je: JsonElement, type: Type,
        jdc: JsonDeserializationContext
    ): LocalTime? {
        val dateAsString = je.asString
        return if (dateAsString.length == 0) {
            null
        } else {
            TIME_FORMAT.parseLocalTime(dateAsString)
        }
    }

    override fun serialize(
        src: LocalTime?, typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val retVal: String
        if (src == null) {
            retVal = ""
        } else {
            retVal = TIME_FORMAT.print(src)
        }
        return JsonPrimitive(retVal)
    }

    companion object {

        private val TIME_FORMAT = ISODateTimeFormat.timeNoMillis()
    }

}