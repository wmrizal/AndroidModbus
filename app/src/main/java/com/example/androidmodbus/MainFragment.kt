package com.example.androidmodbus

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        NukeSSLCerts.nuke()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.my_wow, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        view.findViewById<View>(R.id.button_mainto_first)
            .setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainFragment_to_FirstFragment))
        view.findViewById<View>(R.id.save_file_button).setOnClickListener { _->
            // Gets the data repository in write mode
            val dbHelper = FeedReaderDbHelper(context)
            val db = dbHelper.writableDatabase

    // Create a new map of values, where column names are the keys
            val values = ContentValues().apply {
                put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, "title")
                put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, "subtitle")
            }

    // Insert the new row, returning the primary key value of the new row
            val newRowId = db?.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
            //testfilesave()
            val textView = view.findViewById<View>(R.id.editTextTextPersonName) as TextView
            textView.text = "Created Something in SQLITE"
        }

        view.findViewById<View>(R.id.read_file_button).setOnClickListener { _->//Calling API
            val textView = view.findViewById<View>(R.id.editTextTextPersonName) as TextView
            val queue = Volley.newRequestQueue(getActivity())
            val url = "https://10.0.2.2:3000/boom"//"https://www.google.com"

    // Request a string response from the provided URL.
            val stringRequest = StringRequest(
                Request.Method.POST, url,
                Response.Listener<String> { response ->
                    // Display the first 500 characters of the response string.
                    textView.text = "Response is: ${response.substring(0, if(response.length<500)response.length else 500)}"
                },
                Response.ErrorListener { response ->

                    Log.e("API", response.message?:"" )
                    textView.text = response.message?:"That didn't work!" })
    // Add the request to the RequestQueue.
            queue.add(stringRequest) }


        view.findViewById<View>(R.id.readSQLite).setOnClickListener { _->
            val dbHelper = FeedReaderDbHelper(context)
            val db = dbHelper.readableDatabase

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            val projection = arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE)

// Filter results WHERE "title" = 'My Title'
            val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} = ?"
            val selectionArgs = arrayOf("title")

// How you want the results sorted in the resulting Cursor
            val sortOrder = "${FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

            val cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
            )
            //val itemIds = mutableListOf<Long>()
            var outstring = "Output ids is"
            with(cursor) {
                while (moveToNext()) {
                    val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    //itemIds.add(itemId)
                    outstring += itemId.toString() +","
                }
            }
            val textView = view.findViewById<View>(R.id.editTextTextPersonName) as TextView
            textView.text = outstring
        }
    } /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                view.findViewById<Button>(R.id.button_first).setOnClickListener {
                        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                }
        }*/
}

fun testfileread() {
    File("data.txt").readText()
}
fun testfilesave() {
    File("data.txt").writeText("wow")
}

object NukeSSLCerts {
    internal const val TAG = "NukeSSLCerts"
    fun nuke() {
        try {
            val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
                object : X509TrustManager {

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf<X509Certificate>()
                    }

                    val acceptedIssuers: Array<Any?>
                        get() = arrayOfNulls(0)

                    override fun checkClientTrusted(
                        certs: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun checkServerTrusted(
                        certs: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }
                }
            )
            val sc: SSLContext = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
            HttpsURLConnection.setDefaultHostnameVerifier(object : HostnameVerifier {
                override fun verify(arg0: String?, arg1: SSLSession?): Boolean {
                    return true
                }
            })
        } catch (e: Exception) {
        }
    }
}

object FeedReaderContract {
    // Table contents are grouped together in an anonymous object.
    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "entry"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_SUBTITLE = "subtitle"
    }
}

private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${FeedReaderContract.FeedEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} TEXT," +
            "${FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} TEXT)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FeedReaderContract.FeedEntry.TABLE_NAME}"

class FeedReaderDbHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FeedReader.db"
    }
}