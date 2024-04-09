package com.cs388.socialsync

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class PublicEventDetailActivity : AppCompatActivity() {

    lateinit var event: Event
    fun init() {
        event = intent.getSerializableExtra("event") as Event
        Toast.makeText(this@PublicEventDetailActivity, event.eventName, Toast.LENGTH_LONG).show()


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_event_detail)

        init()
    }
}