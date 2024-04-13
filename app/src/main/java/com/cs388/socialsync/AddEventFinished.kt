package com.cs388.socialsync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddEventFinished : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_event_end)

        val code = findViewById<TextView>(R.id.code)
        val btnFinish = findViewById<AppCompatButton>(R.id.btnFinish)
        val event = Obj.event

        val host = Obj.auth.currentUser!!.uid
        event?.hostUID = host
        event?.participants?.add(host)

        //TODO DELETE THIS **********
        Log.d("EVENT CREATE", event.toString())

        if (event != null) {
            val listener = object : Obj.SetOnDuplicateEventCheckListener {
                override fun onDuplicateEvent() {
                    Toast.makeText(
                        this@AddEventFinished,
                        "Please change event name",
                        Toast.LENGTH_SHORT
                    ).show()
                    val nextIntent= Intent(this@AddEventFinished, AddEventMainActivity::class.java)

                }
                override fun onEventAdded() {
                    Toast.makeText(
                        this@AddEventFinished,
                        "Event added to Database",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            Obj.addEventToDatabase(event, listener)

            btnFinish.setOnClickListener() {
                finish()
            }

        }
    }
}