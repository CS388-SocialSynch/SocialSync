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

    private lateinit var  timeslotAdapter: TimeslotAdapter
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
        timeslotAdapter = TimeslotAdapter(this,eventStarTime, eventEndTime,this)
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
            startTime?.let { it1 -> endTime?.let { it2 -> Obj.setTimes(it1, it2) } }
        }


        cancelButton.setOnClickListener {
            showToast("You have canceled this event")
        }

        // Any initialization or setup code can go here
    }

    override fun onDateSelected(selectedDate: String){
        date = selectedDate
        val availTimes = Obj.event.availability
        val pplCount = Obj.event.participants.size.toFloat()
        for(timeslot in timeslotAdapter.timeslots) {
            val datetime = date + " " + timeslot.time
            if (availTimes.containsKey(datetime)){
                val num =availTimes[datetime]?.size?.toFloat() ?: 0f
                timeslot.opacity= (num/pplCount * 0.8F ) + 0.2F
            }else{
                timeslot.opacity=0.2F
            }
        }
        timeslotAdapter.notifyDataSetChanged()
        if(startTime!=null){
            enableButton(toggleButton)
        } else {
            disableButton(toggleButton)
        }
    }

}