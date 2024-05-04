package com.example.ticketmasterproject

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketmasterService {
    //https://app.ticketmaster.com/discovery/v2/events.json?apikey=0hPuEKvfA0iuFANdUQfAWlj8kbpjmvE9
    @GET("events.json")
    fun getCityAndSizeInfo(
        @Query("city") town: String,
        @Query("size") amount: Int,
        @Query("apikey") apiKey: String,
        @Query("classificationName") classification : String,
        @Query("sort") sort : String) : Call<Data>


}