package com.example.ticketmasterproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class firebaseAdapter(private val eventsInDatabase: ArrayList<firebaseEvent>) : RecyclerView.Adapter<firebaseAdapter.MyViewHolder1>() {
    val db = FirebaseFirestore.getInstance()
    val eventCollection = db.collection("events")
    val currentUser = FirebaseAuth.getInstance().currentUser!!
    val userId = currentUser.uid


    inner class MyViewHolder1 (itemView: View): RecyclerView.ViewHolder (itemView) {
        val name = itemView.findViewById<TextView>(R.id.eventName)
        val address = itemView.findViewById<TextView>(R.id.eventAddress)
        val date = itemView.findViewById<TextView>(R.id.eventDate)
        val venue = itemView.findViewById<TextView>(R.id.eventVenue)
        val thumbnail = itemView.findViewById<ImageView>(R.id.imageView)
        val urlButton = itemView.findViewById<Button>(R.id.seeTickets).setOnClickListener {
            loadURLButton(itemView, eventsInDatabase[adapterPosition].url)
        }
        val price = itemView.findViewById<TextView>(R.id.price)
        val addText = itemView.findViewById<Button>(R.id.add)

        val addBtn = itemView.findViewById<Button>(R.id.add).setOnClickListener {
            eventCollection.whereEqualTo("eventId",eventsInDatabase[adapterPosition].eventId).whereEqualTo("userId",userId).get().addOnSuccessListener{documents->
                for(document in documents){
                    document.reference.delete()
                }
                val selectedItem = adapterPosition
                eventsInDatabase.removeAt(selectedItem)
                notifyItemRemoved(selectedItem)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder1 {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_layout, parent, false)
        return MyViewHolder1(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder1, position: Int) {

        val currentItem = eventsInDatabase[position]
        holder.name.text = currentItem.name
        holder.venue.text = currentItem.venue
        holder.date.text = currentItem.date
        holder.address.text = currentItem.address
        holder.price.text = currentItem.prices

        val context = holder.itemView.context
        Glide.with(context)
            .load(currentItem.thumbnail)
            .into(holder.thumbnail)

        holder.addText.text = "remove"


    }

    override fun getItemCount(): Int {
        return eventsInDatabase.size
    }
}