package com.cs388.socialsync

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

class PublicEventDetailActivity : AppCompatActivity() {

    lateinit var event: Event
    private lateinit var eventNameTextView: TextView
    private lateinit var eventDateTextView: TextView
    private lateinit var eventLocationTextView: TextView
    private lateinit var eventTimeTextView: TextView


    fun init() {
        event = intent.getSerializableExtra("event") as Event
        Toast.makeText(this@PublicEventDetailActivity, event.eventName, Toast.LENGTH_LONG).show()
        eventNameTextView = findViewById(R.id.eventDetailEvent);
        eventDateTextView = findViewById(R.id.dateDetailEvent);
        eventLocationTextView = findViewById(R.id.locationNameDetailEvent);
        eventTimeTextView = findViewById(R.id.timeDetailEvent);


        eventNameTextView.text = event.eventName;
        eventDateTextView.text = event.date;
        eventLocationTextView.text = event.address;
        eventTimeTextView.text = "${event.startTime} - ${event.endTime}"










    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_event_detail)

        init()
    }
}