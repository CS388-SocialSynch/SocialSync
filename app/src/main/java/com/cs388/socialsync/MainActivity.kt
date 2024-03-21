package com.cs388.socialsync

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = Firebase.database.reference


        fun writeNewUser(userId: String, name: String, email: String) {
            val user = User(name, email)

            database.child("users").child(userId).setValue(user)
        }

        writeNewUser("1111", "Cram", "cram@cramer.com")





    }

    @IgnoreExtraProperties
    data class User(val username: String? = null, val email: String? = null) {
        // Null default values create a no-argument default constructor, which is needed
        // for deserialization from a DataSnapshot.
    }
}