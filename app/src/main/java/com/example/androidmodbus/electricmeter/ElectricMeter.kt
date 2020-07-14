package com.example.androidmodbus.electricmeter

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.view.View
import android.widget.TextView
import com.example.androidmodbus.R
import com.example.androidmodbus.electricmeter.MeterReaderContract.MeterReadEntry.TABLE_NAME
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZonedDateTime
import kotlinx.serialization.internal.*
import kotlinx.serialization.json.JsonInput
import kotlinx.serialization.json.JsonObject

@Serializer(forClass = ZonedDateTime::class)
object ZonedDateTimeSerializer: KSerializer<ZonedDateTime> {
    //private val df: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS")

    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("WithCustomDefault", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, obj: ZonedDateTime) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        return ZonedDateTime.parse(decoder.decodeString())
    }
}

@Serializable
data class ElectricMeterReading(var machineId : Int? = null,
                                @Serializable(with=ZonedDateTimeSerializer::class)
                                var timestamp : ZonedDateTime? = null,
                                var meterValue : Int? = null) {
}

object MeterReaderContract {
    // Table contents are grouped together in an anonymous object.
    object MeterReadEntry : BaseColumns {
        const val TABLE_NAME = "electric_meter"
        const val COLUMN_NAME_MACHINE_ID = "machine_id"
        const val COLUMN_NAME_TIMESTAMP = "timestamp"
        const val COLUMN_NAME_METER_VALUE="meter_value"
    }

    private const val SQL_CREATE_ENTRIES =
        "CREATE TABLE IF NOT EXISTS ${MeterReadEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${MeterReadEntry.COLUMN_NAME_MACHINE_ID} INTEGER," +
                "${MeterReadEntry.COLUMN_NAME_TIMESTAMP} TEXT," +
                "${MeterReadEntry.COLUMN_NAME_METER_VALUE} INTEGER) "

    private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${MeterReadEntry.TABLE_NAME}"

    class MeterReaderDbHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(MeterReaderContract.SQL_CREATE_ENTRIES)
        }
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(MeterReaderContract.SQL_DELETE_ENTRIES)
            onCreate(db)
        }
        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            onUpgrade(db, oldVersion, newVersion)
        }
        companion object {
            // If you change the database schema, you must increment the database version.
            const val DATABASE_VERSION = 1
            const val DATABASE_NAME = "MeterReader.db"
        }
    }
}



fun ReadElectricMeterDatabase(context: Context?,view: View){
    val dbHelper = MeterReaderContract.MeterReaderDbHelper(context)
    val db = dbHelper.readableDatabase

// Define a projection that specifies which columns from the database
// you will actually use after this query.
    val projection = null//arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE)

// Filter results WHERE "title" = 'My Title'
    val selection = "${MeterReaderContract.MeterReadEntry.COLUMN_NAME_MACHINE_ID} = ?"
    val selectionArgs = arrayOf("1")

// How you want the results sorted in the resulting Cursor
    val sortOrder = "${MeterReaderContract.MeterReadEntry.COLUMN_NAME_TIMESTAMP} DESC"

    val cursor = db.query(
        TABLE_NAME,   // The table to query
        projection,             // The array of columns to return (pass null to get all)
        selection,              // The columns for the WHERE clause
        selectionArgs,          // The values for the WHERE clause
        null,                   // don't group the rows
        null,                   // don't filter by row groups
        sortOrder               // The sort order
    )
    //val itemIds = mutableListOf<Long>()
    var displayString: String = "Output ids is"
    with(cursor) {
        while (moveToNext()) {
            val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))

            val itemTimestampString = getString(getColumnIndexOrThrow(MeterReaderContract.MeterReadEntry.COLUMN_NAME_TIMESTAMP))
            val itemTimestamp = ZonedDateTime.parse(itemTimestampString)
            //itemIds.add(itemId)
            displayString += "$itemId,${itemTimestamp.toString()}\n"
        }
    }
    val textView = view.findViewById<View>(R.id.editTextTextPersonName) as TextView
    textView.text = displayString
}


