package com.support.dateClass

import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonDeserializationContext
import org.joda.time.format.ISODateTimeFormat
import com.google.gson.JsonSerializer
import com.google.gson.JsonDeserializer
import org.joda.time.LocalDate
import java.lang.reflect.Type


class LocalDateSerializer : JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {

    @Throws(JsonParseException::class)
    override fun deserialize(
        je: JsonElement, type: Type,
        jdc: JsonDeserializationContext
    ): LocalDate? {
        val dateAsString = je.asString
        return if (dateAsString.length == 0) {
            null
        } else {
            DATE_FORMAT.parseLocalDate(dateAsString)
        }
    }

    override fun serialize(
        src: LocalDate?, typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val retVal: String
        if (src == null) {
            retVal = ""
        } else {
            retVal = DATE_FORMAT.print(src)
        }
        return JsonPrimitive(retVal)
    }

    companion object {
        private val DATE_FORMAT = ISODateTimeFormat.date()
    }
}