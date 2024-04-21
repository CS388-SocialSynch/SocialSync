package com.cs388.socialsync

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class ChooseTimeActivity: ChXXXeTimeActivity()  {

    private lateinit var timeslotAdapter:TimeslotPlusAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_time)
        loadDBData()
        startTime = "OOF"
        val chooseDatesRecyclerView = findViewById<RecyclerView>(R.id.chooseDateRecyclerView)
        chooseDatesRecyclerView.layoutManager=
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        dateAdapter = DateAdapter(this, eventDates, this, Obj.event.useSpecificDate)
        chooseDatesRecyclerView.adapter=dateAdapter

        val chooseTimeslotRecyclerView = findViewById<RecyclerView>(R.id.chooseTimeslotsRecyclerView)
        chooseTimeslotRecyclerView.layoutManager = LinearLayoutManager(this)
        timeslotAdapter = TimeslotPlusAdapter(this, eventStarTime, eventEndTime,this)
        chooseTimeslotRecyclerView.adapter = timeslotAdapter

        toggleButton = findViewById<Button>(R.id.saveTimesButton)
        toggleButton.setOnClickListener {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd")
            val outputFormat = SimpleDateFormat("MM/dd")
            if (date!=null ) {
                var formattedDate = date
                if(Obj.event.useSpecificDate) {
                    formattedDate = outputFormat.format(inputFormat.parse(date))
                }
                Obj.addAvailability(date as String, times)
                showToast("Saved times for $formattedDate")
            }

        }
        val backButton: Button = findViewById<Button>(R.id.chooseTimeBackButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onDateSelected(selectedDate: String){
        date = selectedDate
        val availTimes = Obj.event.availability

        for(timeslot in timeslotAdapter.timeslots){
            val datetime = date+" "+timeslot.time
            Log.d("CHECK",datetime)

            if(availTimes.containsKey(date+" "+timeslot.time))
                timeslot.isSelected = availTimes[datetime]?.contains(Obj.loggedUserID) ?: false
        }
        timeslotAdapter.notifyDataSetChanged()

        if(startTime!=null){
            enableButton(toggleButton)
        } else {
            disableButton(toggleButton)
        }
    }
    override fun onTimeslotsSelected(selectedTimeslots: List<String>) {
        times = selectedTimeslots
        if ( date != null) {
                enableButton(toggleButton)
        } else { disableButton(toggleButton) }
    }
}