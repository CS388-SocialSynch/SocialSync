package com.cs388.socialsync

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

class AddEventFinished:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_event_end)

        val code = findViewById<TextView>(R.id.code)
        val btnFinish = findViewById<AppCompatButton>(R.id.btnFinish)
        val event = intent.getBundleExtra("eventInfo")?.getSerializable(EVENT_ITEM) as? Event

        //TODO DELETE THIS **********
        Log.d("EVENT CREATE",  event.toString())

        // TODO have the DAO work here
        val DAO = DAOEvent()
        DAO.dataBaseRef= FirebaseDatabase.getInstance().getReference("EVENTS")
        val dataBaseRef = DAO.dataBaseRef

        dataBaseRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if(event != null){
                DAO.add(event)
                //TODO DELETE THIS **********
                Log.d("DATA",  event.toString())
            }
            Toast.makeText(applicationContext, "data added", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Fail to add data $error", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        btnFinish.setOnClickListener(){
            finish()
        }


    }
}