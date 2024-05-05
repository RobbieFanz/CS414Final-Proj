package com.example.ticketmasterproject.ui.home

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ticketmasterproject.Data
import com.example.ticketmasterproject.Event
import com.example.ticketmasterproject.EventsAdapter
import com.example.ticketmasterproject.R
import com.example.ticketmasterproject.TicketmasterService
import com.example.ticketmasterproject.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {



    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        root.findViewById<Button>(R.id.search).setOnClickListener {
            hideKeyboard()
            val city = root.findViewById<EditText>(R.id.cityEdit).text.toString()
            val classification = root.findViewById<EditText>(R.id.eventEdit).text.toString()
            var message = ""
            var bool = true
            if(city == "" && classification == ""){
                message = "Please enter a city and an event type"

            } else if (city == ""){
                message = "Please enter a city"

            }else if (classification == ""){
                message = "please enter an event type"

            }else{
                eventList.clear()
                // I know there are two of these one after the clear and one after the add but it best like this putting this after the apicall function was not working consistantly
                adapter.notifyDataSetChanged()
                APICall(adapter, ticketmasterAPI, eventList, city, classification)
                bool = false
            }
            if(bool) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Warning")
                builder.setMessage(message)
                builder.setPositiveButton("Ok") { _, _ ->

                }

                val dialog = builder.create()
                dialog.show()
            }

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


fun hideKeyboard() {
    val i = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) {
        view = View(this)
    }
    i.hideSoftInputFromWindow(view.windowToken, 0)
}

fun APICall (adapter: EventsAdapter, ticketmasterAPI: TicketmasterService, eventList: ArrayList<Event>, city : String, classification: String, view: View){
    ticketmasterAPI.getCityAndSizeInfo(city, 20, "0hPuEKvfA0iuFANdUQfAWlj8kbpjmvE9", classification, "date,asc").enqueue(object : Callback<Data> {


        override fun onResponse(call: Call<Data>, response: Response<Data>) {
            Log.d(ContentValues.TAG, "onResponse: $response")

            val body = response.body()
            if (body == null) {
                Log.w(ContentValues.TAG, "Valid response was not received")
                return
            }
            val embeddedData = body._embedded
            if (embeddedData == null || body == null) {
                view.findViewById<TextView>(R.id.noEventsText).visibility = View.VISIBLE
            }else {
                view.findViewById<TextView>(R.id.noEventsText).visibility = View.INVISIBLE
                eventList.addAll(body._embedded.events)
                adapter.notifyDataSetChanged()
            }

        }

        override fun onFailure(call: Call<Data>, t: Throwable) {
            Log.d(ContentValues.TAG, "onFailure : $t")
        }

    })
}

