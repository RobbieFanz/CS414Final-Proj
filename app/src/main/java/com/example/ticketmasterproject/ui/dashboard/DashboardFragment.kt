package com.example.ticketmasterproject.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ticketmasterproject.R
import com.example.ticketmasterproject.RegisterActivity
import com.example.ticketmasterproject.databinding.FragmentDashboardBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment() {
    val currentUser = FirebaseAuth.getInstance().currentUser!!
    val userDisplayName = currentUser.displayName
    val userEmail = currentUser.email


    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        root.findViewById<TextView>(R.id.nameTextView).text = userDisplayName
        root.findViewById<TextView>(R.id.emailTextView).text = userEmail
        root.findViewById<Button>(R.id.sign_out).setOnClickListener { signOut() }
        root.findViewById<Button>(R.id.delete).setOnClickListener { deleteAccount() }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun deleteAccount(){
        val userUID = currentUser.uid

        currentUser.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("events")
                        .whereEqualTo("userId",userUID)
                        .get()
                        .addOnSuccessListener{ documents->
                        for(document in documents){
                            document.reference.delete()
                        }
                    }
                    startRegisterActivity()
                }
            }
    }

    fun signOut(){
        Firebase.auth.signOut()
        startRegisterActivity()
    }

    private fun startRegisterActivity(){
        val intent = Intent(activity, RegisterActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}