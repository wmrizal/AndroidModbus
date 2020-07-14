package com.example.androidmodbus

import android.util.Log
import com.example.androidmodbus.electricmeter.ElectricMeterReading
import com.example.androidmodbus.electricmeter.MeterReaderContract
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDateTime
import java.time.ZonedDateTime
import kotlinx.serialization.*
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@Serializable
data class Data(val a: Int, val b: String = "42")

@ImplicitReflectionSerializer
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test fun checkJsonforElectricMeterReading(){
        val test = ElectricMeterReading()
        test.machineId=1
        test.meterValue=300
        test.timestamp= ZonedDateTime.now().plus(1,ChronoUnit.HOURS)

        val json = Json(JsonConfiguration.Stable)
        val jsonData = json.stringify(ElectricMeterReading.serializer(),test)
        println((test.timestamp?:ZonedDateTime.now()).dayOfWeek.toString());
        val tnow = Instant.now().plus(1,ChronoUnit.HOURS)
        println(tnow.atZone(ZoneId.of("UTC")).dayOfWeek.toString())
    }
}