package com.example.androidmodbus.gsonlocaldatetime

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class LocalDateTimeSerializer : JsonSerializer<LocalDateTime?> {
    override fun serialize(
        localDateTime: LocalDateTime?,
        srcType: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(formatter.format(localDateTime))
    }

    companion object {
        private val formatter =
            DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss")
    }
}