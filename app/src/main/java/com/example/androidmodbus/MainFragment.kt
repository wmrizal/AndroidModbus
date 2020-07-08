package com.example.androidmodbus

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.android.volley.toolbox.Volley
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.my_wow, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        view.findViewById<View>(R.id.button_mainto_first)
            .setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainFragment_to_FirstFragment))
        view.findViewById<View>(R.id.save_file_button).setOnClickListener(
            View.OnClickListener {  x-> testfilesave() }
        )

        view.findViewById<View>(R.id.read_file_button).setOnClickListener(
            View.OnClickListener {  x->
                val textView = view.findViewById<View>(R.id.editTextTextPersonName)
                val queue = Volley.newRequestQueue()
                val url = "https://www.google.com"

// Request a string response from the provided URL.
                val stringRequest = StringRequest(Request.Method.GET, url,
                    Response.Listener<String> { response ->
                        // Display the first 500 characters of the response string.
                        textView.text = "Response is: ${response.substring(0, 500)}"
                    },
                    Response.ErrorListener { textView.text = "That didn't work!" })

// Add the request to the RequestQueue.
                queue.add(stringRequest)() }
        )
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
