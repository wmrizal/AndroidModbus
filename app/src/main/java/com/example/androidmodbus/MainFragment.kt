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
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.androidmodbus.electricmeter.ElectricMeterReading
import com.example.androidmodbus.electricmeter.MeterReaderContract
import com.example.androidmodbus.electricmeter.ReadElectricMeterDatabase
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.time.Instant
import java.time.ZonedDateTime
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
            val dbHelper = MeterReaderContract.MeterReaderDbHelper(context)
            val db = dbHelper.writableDatabase

    // Create a new map of values, where column names are the keys
            val values = ContentValues().apply {
                put(MeterReaderContract.MeterReadEntry.COLUMN_NAME_MACHINE_ID, 1)
                put(MeterReaderContract.MeterReadEntry.COLUMN_NAME_METER_VALUE, 100)
                put(MeterReaderContract.MeterReadEntry.COLUMN_NAME_TIMESTAMP, Instant.now().toEpochMilli())
            }

    // Insert the new row, returning the primary key value of the new row
            /*val newRowId = */db?.insert(MeterReaderContract.MeterReadEntry.TABLE_NAME, null, values)

            val textView = view.findViewById<View>(R.id.editTextTextPersonName) as TextView
            textView.text = "Created Something in SQLITE"
        }

        view.findViewById<View>(R.id.read_file_button).setOnClickListener { _->//Calling API
            val textView = view.findViewById<View>(R.id.editTextTextPersonName) as TextView
            val queue = Volley.newRequestQueue(getActivity())
            val url = "https://10.0.2.2:3000/boom"//"https://www.google.com"

    // Request a string response from the provided URL.
            //val jsonRequest = JsonRequest(Request.Method.POST,url,)
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
            //FeederReadDatabase(context,view)
            ReadElectricMeterDatabase(context,view)
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


