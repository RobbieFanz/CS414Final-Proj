package com.example.ticketmasterproject

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ticketmasterproject.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates


fun loadURLButton (view: View, url: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW)

    browserIntent.data = Uri.parse(url)
    view.context.startActivity(browserIntent)
}

fun parseTime(time: String): String {
    val timeArray = time.split(":")
    var hour = timeArray[0].toInt()
    var amPm = "am"
    if(hour>12){
        hour = hour-12
        amPm = "pm"
    }
    return "$hour:${timeArray[1]}$amPm"

}


class EventsAdapter(private val events: ArrayList<Event>) : RecyclerView.Adapter<EventsAdapter.MyViewHolder>() {
    val db = FirebaseFirestore.getInstance()
    val eventsInDatabase = db.collection("events")
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid


    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder (itemView) {
        var inDatabase = false
        val name = itemView.findViewById<TextView>(R.id.eventName)
        val address = itemView.findViewById<TextView>(R.id.eventAddress)
        val date = itemView.findViewById<TextView>(R.id.eventDate)
        val venue = itemView.findViewById<TextView>(R.id.eventVenue)
        val thumbnail = itemView.findViewById<ImageView>(R.id.imageView)
        val urlButton = itemView.findViewById<Button>(R.id.seeTickets).setOnClickListener {
            loadURLButton(itemView, events[layoutPosition].url)
        }
        val price = itemView.findViewById<TextView>(R.id.price)


        val addText = itemView.findViewById<Button>(R.id.add)
        init {

        }

        val addBtn = itemView.findViewById<Button>(R.id.add).setOnClickListener {
            val currentItem = events[layoutPosition]
            var btnText = "add"
            if (inDatabase) {
                eventsInDatabase.whereEqualTo("eventId",currentItem.id).whereEqualTo("userId",userId).get().addOnSuccessListener{documents->
                    for(document in documents){
                        document.reference.delete()
                    }
                }

                btnText = "add"
                inDatabase=false

            } else {
                val event = hashMapOf(
                    "name" to name.text.toString(),
                    "address" to address.text.toString(),
                    "date" to date.text.toString(),
                    "venue" to venue.text.toString(),
                    //when I put events[layoutPosition] into a variable it would crash the app
                    "thumbnail" to currentItem.images.maxByOrNull {
                        it.width * it.height
                    },
                    "url" to currentItem.url,
                    "prices" to price.text.toString(),
                    "eventId" to currentItem.id,
                    "userId" to userId
                )
                val documentId = eventsInDatabase.document().id
                eventsInDatabase.document(documentId).set(event)
                btnText = "added"
                inDatabase=true
            }
            addText.text = btnText
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_layout, parent, false)
        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = events[position]
        holder.name.text = currentItem.name
        holder.venue.text = currentItem._embedded.venues.get(0).name
        holder.date.text = "${currentItem.dates.start.localDate} @${parseTime(currentItem.dates.start.localTime)}"
        holder.address.text = "${currentItem._embedded.venues.get(0).address.line1}, ${currentItem._embedded.venues.get(0).city.name}, ${currentItem._embedded.venues.get(0).state.name}"

        if(currentItem.priceRanges == null){
            holder.price.text =""
        }else {
            holder.price.text =
                "Price Range: $${currentItem.priceRanges.get(0).min} - $${currentItem.priceRanges.get(0).max}"
        }

        val highestQualityImage = currentItem.images.maxByOrNull {
            it.width * it.height
        }

        val context = holder.itemView.context

        if (highestQualityImage != null) {
            Glide.with(context)
                .load(highestQualityImage.url)
                .into(holder.thumbnail)
        }
        var btnText = "add"
        holder.inDatabase=false
        eventsInDatabase.whereEqualTo("eventId",currentItem.id).whereEqualTo("userId", userId).get()
            .addOnSuccessListener { documents ->
                // Iterate all the documents
                if (!documents.isEmpty()) {
                    btnText = "added"
                    holder.inDatabase=true
                }
                holder.addText.text = btnText
            }

    }

    override fun getItemCount(): Int {
        return events.size
    }

}