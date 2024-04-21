package com.cs388.socialsync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ChangeTimeActivity: ChXXXeTimeActivity() , OnTimeslotSelectionListener, OnDateSelectionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_time)
        loadDBData()


        val settingsButton = findViewById<Button>(R.id.changeSettingsButton)
        toggleButton = findViewById<Button>(R.id.setTimeButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val propTextView = findViewById<TextView>(R.id.eventPropertiesTextView)







        val datesRecyclerView = findViewById<RecyclerView>(R.id.dateRecyclerView)
        datesRecyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        dateAdapter = DateAdapter(this, eventDates, this, Obj.event.useSpecificDate)
        datesRecyclerView.adapter=dateAdapter

        val timeslotRecyclerView = findViewById<RecyclerView>(R.id.timeslotsRecyclerView)
        timeslotRecyclerView.layoutManager = LinearLayoutManager(this)
        val timeslotAdapter = TimeslotAdapter(this,eventStarTime, eventEndTime,this)
        timeslotRecyclerView.adapter = timeslotAdapter



        settingsButton.setOnClickListener {
//            Log.e("LETS TRY THIS", "CHECK " + Obj.event)
            val intent = Intent(this@ChangeTimeActivity, AddEventMainActivity::class.java)
            startActivity(intent)
        }

        //setEventButton.isEnabled = false
        //setEventButton.alpha = 0.5f
        toggleButton.setOnClickListener {
            if(Obj.event.useSpecificDate)
                propTextView.text = "Date:$date\nStart Time: $startTime\nEnd Time: $endTime \nJoin CODE: ${Obj.event.eventCode}"
            else {
                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val currDate = LocalDate.parse(date, inputFormatter)
                val dayOfWeek = currDate.dayOfWeek.toString().lowercase().capitalize()
                propTextView.text = "Date:$dayOfWeek\nStart Time: $startTime\nEnd Time: $endTime \nJoin CODE: ${Obj.event.eventCode}"
            }
        }


        cancelButton.setOnClickListener {
            showToast("You have canceled this event")
        }

        // Any initialization or setup code can go here
    }
}