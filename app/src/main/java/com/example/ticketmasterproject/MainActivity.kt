package com.example.ticketmasterproject


import android.content.Intent

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ticketmasterproject.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser

        // If currentUser is null, open the RegisterActivity
        if (currentUser == null) {
            startRegisterActivity()
        } else {

            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navView: BottomNavigationView = binding.navView


            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_dashboard, R.id.navigation_home, R.id.navigation_notifications
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
    }

    private fun startRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        
    }

}