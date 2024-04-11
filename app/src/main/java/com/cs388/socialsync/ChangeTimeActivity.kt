package com.cs388.socialsync

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChangeTimeActivity: AppCompatActivity() {

    private lateinit var timeslotAdapter: TimeslotAdapter
    private lateinit var dateAdapter: DateAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_time)

        val settingsButton = findViewById<Button>(R.id.changeSettingsButton)
        val setEventButton = findViewById<Button>(R.id.setTimeButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)


        /*Removed Next Button
        val prevButton = findViewById<ImageButton>(R.id.prevButton)
        val nextButton = findViewById<ImageButton>(R.id.nextButton)
        val blackColor = ContextCompat.getColor(this, android.R.color.black)
        nextButton.setColorFilter(blackColor, PorterDuff.Mode.SRC_IN)
        prevButton.setColorFilter(blackColor,PorterDuff.Mode.SRC_IN)
        */
        val datesRecyclerView = findViewById<RecyclerView>(R.id.dateRecyclerView)
        datesRecyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        dateAdapter = DateAdapter(this, "2024-03-28", "2024-04-05")
        datesRecyclerView.adapter=dateAdapter

        val timeslotRecyclerView = findViewById<RecyclerView>(R.id.timeslotsRecyclerView)
        timeslotRecyclerView.layoutManager = LinearLayoutManager(this)
        timeslotAdapter = TimeslotAdapter(this, "8:00 AM", "11:00 AM")
        timeslotRecyclerView.adapter = timeslotAdapter



        settingsButton.setOnClickListener {
            showToast("Open Ethan's Activity")
        }


        //setEventButton.isEnabled = false
        //setEventButton.alpha = 0.5f


        cancelButton.setOnClickListener {
            showToast("You have canceled this event")
        }

        // Any initialization or setup code can go here
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}