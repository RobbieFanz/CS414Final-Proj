package com.example.ticketmasterproject.ui.home

import android.app.Activity
import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ticketmasterproject.Data
import com.example.ticketmasterproject.Event
import com.example.ticketmasterproject.EventsAdapter
import com.example.ticketmasterproject.R
import com.example.ticketmasterproject.TicketmasterService
import com.example.ticketmasterproject.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val BASE_URL = "https://app.ticketmaster.com/discovery/v2/"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View {
        var db = FirebaseFirestore.getInstance()


        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val eventList = ArrayList<Event>()

        val adapter = EventsAdapter(eventList)

        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.adapter = adapter


        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val ticketmasterAPI = retrofit.create(TicketmasterService::class.java)

        root.findViewById<Button>(R.id.search).setOnClickListener {
            hideKeyboard(root)
            val city = root.findViewById<EditText>(R.id.cityEdit).text.toString()
            val classification = root.findViewById<EditText>(R.id.eventEdit).text.toString()
            var message = ""
            var bool = true
            if (city == "" && classification == "") {
                message = "Please enter a city and an event type"

            } else if (city == "") {
                message = "Please enter a city"

            } else if (classification == "") {
                message = "please enter an event type"

            } else {
                eventList.clear()
                // I know there are two of these one after the clear and one after the add but it best like this putting this after the apicall function was not working consistantly
                adapter.notifyDataSetChanged()
                APICall(adapter, ticketmasterAPI, eventList, city, classification, root)
                bool = false
            }
            if (bool) {
                val builder = AlertDialog.Builder(requireContext())
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


    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun APICall(
        adapter: EventsAdapter,
        ticketmasterAPI: TicketmasterService,
        eventList: ArrayList<Event>,
        city: String,
        classification: String,
        view: View
    ) {
        ticketmasterAPI.getCityAndSizeInfo(
            city,
            20,
            "0hPuEKvfA0iuFANdUQfAWlj8kbpjmvE9",
            classification,
            "date,asc"
        ).enqueue(object : Callback<Data> {


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
                } else {
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
}

