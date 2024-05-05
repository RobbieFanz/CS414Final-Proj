package com.example.ticketmasterproject

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ticketmasterproject.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    fun APICall (adapter: EventsAdapter, ticketmasterAPI: TicketmasterService, eventList: ArrayList<Event>, city : String, classification: String){
        ticketmasterAPI.getCityAndSizeInfo(city, 20, "0hPuEKvfA0iuFANdUQfAWlj8kbpjmvE9", classification, "date,asc").enqueue(object :
            Callback<Data> {


            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                Log.d(TAG, "onResponse: $response")

                val body = response.body()
                if (body == null) {
                    Log.w(TAG, "Valid response was not received")
                    return
                }
                val embeddedData = body._embedded
                if (embeddedData == null || body == null) {
                    findViewById<TextView>(R.id.noEventsText).visibility = View.VISIBLE
                }else {
                    findViewById<TextView>(R.id.noEventsText).visibility = View.INVISIBLE
                    eventList.addAll(body._embedded.events)
                    adapter.notifyDataSetChanged()
                }

            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Log.d(TAG, "onFailure : $t")
            }

        })
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}