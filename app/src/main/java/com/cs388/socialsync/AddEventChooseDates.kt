package com.cs388.socialsync

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class AddEventChooseDates: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_event_choosing_dates)

        val rv = findViewById<RecyclerView>(R.id.specificDates)

        val event = intent.getBundleExtra("eventInfo")?.getSerializable(EVENT_ITEM) as? Event
        Log.d("Choose", event.toString())

    }
}

//TODO
// add a back button --> discards current work for specific days and goes back to last event --> dont finish chain
// click done then go back to part 2 for final steps