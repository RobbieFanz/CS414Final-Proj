package com.example.ticketmasterproject

data class Data(
    val _embedded: EmbeddedData
)

data class EmbeddedData(
    val events: List<Event>
)

data class firebaseEvent(
    val eventId: String,
    val name: String,
    val address: String,
    val date: String,
    val venue: String,
    val thumbnail: String,
    val url: String,
    val prices: String
)
data class Event(
    val id: String,
    val name: String,
    val address: String,
    val dates: Dates,
    val _embedded: Venues,
    val images: List<Image>,
    val url: String,
    val priceRanges: List<Price>
)

data class Price(
    val max: Double,
    val min: Double
)

data class Image(
    val url: String,
    val height: Int,
    val width: Int
)

data class Venues(
    val venues: List<Venue>
)

data class Venue(
    val name: String,
    val address: Address,
    val city: City,
    val state: State,

    )

data class Dates(
    val start: StartTime
)

data class StartTime(
    val localDate: String,
    val localTime: String
)

data class Address(
    val line1: String
)

data class State(
    val name: String
)

data class City(
    val name: String
)