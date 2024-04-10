package com.cs388.socialsync

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddEventSelectDate:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_event_dates)

        val event = intent.getBundleExtra("eventInfo")?.getSerializable(EVENT_ITEM) as? Event

        Toast.makeText(applicationContext, event?.eventName.toString(),Toast.LENGTH_SHORT).show()

    }
}