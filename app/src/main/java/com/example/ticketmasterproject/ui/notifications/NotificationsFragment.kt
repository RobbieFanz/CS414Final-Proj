package com.example.ticketmasterproject.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ticketmasterproject.R
import com.example.ticketmasterproject.databinding.FragmentNotificationsBinding
import com.example.ticketmasterproject.firebaseAdapter
import com.example.ticketmasterproject.firebaseEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class NotificationsFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser!!
    val userId = currentUser.uid
    val eventsInDatabase = db.collection("events").whereEqualTo("userId", userId)

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val firebaseEventList = ArrayList<firebaseEvent>()

        eventsInDatabase.get()
            .addOnSuccessListener { documents ->
                val recyclerView = root.findViewById<RecyclerView>(R.id.firebase_recycle)
                for (document in documents) {
                    val firebaseEventInstance = firebaseEvent(
                        eventId = document.getString("eventId") ?: "",
                        name = document.getString("name") ?: "",
                        address = document.getString("address") ?: "",
                        date = document.getString("date") ?: "",
                        venue = document.getString("venue") ?: "",
                        thumbnail = document.getString("thumbnail") ?: "",
                        url = document.getString("url") ?: "",
                        prices = document.getString("prices") ?: ""
                    )
                    // Access each document here
                    firebaseEventList.add(firebaseEventInstance)
                    // Do something with eventData
                }
                val adapter = firebaseAdapter(firebaseEventList)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}