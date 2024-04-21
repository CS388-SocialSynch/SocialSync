package com.cs388.socialsync

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.cs388.socialsync.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityMainBinding
    private lateinit var dashboardFragment: Fragment
    private lateinit var eventsFragment: Fragment
    private lateinit var profileFragment: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        createNotificationChannel();

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        database = Firebase.database.reference


        fun writeNewUser(userId: String, name: String, email: String) {
            val user = User(name, email)

            database.child("users").child(userId).setValue(user)
        }

        //writeNewUser("1112", "Cram", "cram@cramer.com")

        eventsFragment = EventsFragment() // Initialize homeFragment
        dashboardFragment = DashboardFragment()
        profileFragment = ProfileFragment()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        // handle navigation selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.nav_dashboard -> dashboardFragment
                R.id.nav_events -> eventsFragment
                R.id.nav_profile -> profileFragment

                else -> throw IllegalArgumentException("Invalid item ID")
            }
            replaceFragment(selectedFragment)
            true
        }

        // Set default selection
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.nav_dashboard
        }

    }



    @IgnoreExtraProperties
    data class User(val username: String? = null, val email: String? = null) {
        // Null default values create a no-argument default constructor, which is needed
        // for deserialization from a DataSnapshot.
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame_layout, fragment)
        fragmentTransaction.commit()
    }



}
