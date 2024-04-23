package com.cs388.socialsync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        Log.d("ST",eventStarTime)
        if(Obj.event.date != "") {
            var formattedDate = ""
            if (!Obj.event.useSpecificDate){

                formattedDate = when (Obj.event.date) {
                    "MON" -> "Monday"
                    "TUE" -> "Tuesday"
                    "WED" -> "Wednesday"
                    "THU" -> "Thursday"
                    "FRI" -> "Friday"
                    "SAT" -> "Saturday"
                    "SUN" -> "Sunday"
                    else -> ""
                }
                val s = convertMilitaryToAMPM(Obj.event.startTime)
                val e = convertMilitaryToAMPM(Obj.event.endTime)
                propTextView.text =
                    "Date:$formattedDate\nStart Time: $s\nEnd Time: $e \nJoin CODE: ${Obj.event.eventCode}"
            } else {

                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                val currDate = LocalDate.parse(Obj.event.date, inputFormatter)
                val dayOfWeek = currDate.dayOfWeek.toString().lowercase().capitalize()
                formattedDate =  LocalDate.parse(Obj.event.date, DateTimeFormatter.ISO_LOCAL_DATE)
                    .format(dateFormatter)
                propTextView.text = "Date:$dayOfWeek, $formattedDate\nStart Time: ${Obj.event.startTime}\nEnd Time: ${Obj.event.endTime} \nJoin CODE: ${Obj.event.eventCode}"
            }
        } else {
            propTextView.text = "Date:\nStart Time:\nEnd Time:\nJoin CODE: ${Obj.event.eventCode}"
        }





        val datesRecyclerView = findViewById<RecyclerView>(R.id.dateRecyclerView)
        datesRecyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        dateAdapter = DateAdapter(this, eventDates, this, Obj.event.useSpecificDate)
        datesRecyclerView.adapter=dateAdapter

        val timeslotRecyclerView = findViewById<RecyclerView>(R.id.timeslotsRecyclerView)
        timeslotRecyclerView.layoutManager = LinearLayoutManager(this)
        timeslotAdapter = TimeslotAdapter(this,eventStarTime, eventEndTime,this)
        timeslotRecyclerView.adapter = timeslotAdapter



        settingsButton.setOnClickListener {
            Obj.updateEventOldName = Obj.event.eventName
            val intent = Intent(this@ChangeTimeActivity, AddEventMainActivity::class.java)
            startActivity(intent)
        }

        //setEventButton.isEnabled = false
        //setEventButton.alpha = 0.5f
        toggleButton.setOnClickListener {
            var formattedDate = ""
            if (!Obj.event.useSpecificDate){

                formattedDate = when (date) {
                    "MON" -> "Monday"
                    "TUE" -> "Tuesday"
                    "WED" -> "Wednesday"
                    "THU" -> "Thursday"
                    "FRI" -> "Friday"
                    "SAT" -> "Saturday"
                    "SUN" -> "Sunday"
                    else -> "Invalid day"
                }
                propTextView.text =
                    "Date:$formattedDate\nStart Time: $startTime\nEnd Time: $endTime \nJoin CODE: ${Obj.event.eventCode}"
            } else {

                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                val currDate = LocalDate.parse(date, inputFormatter)
                val dayOfWeek = currDate.dayOfWeek.toString().lowercase().capitalize()
                formattedDate =  LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
                    .format(dateFormatter)
                propTextView.text = "Date:$dayOfWeek, $formattedDate\nStart Time: $startTime\nEnd Time: $endTime \nJoin CODE: ${Obj.event.eventCode}"
            }
            startTime?.let { it1 -> endTime?.let { it2 -> date?.let { it3 ->
                Obj.setTimes(it1, it2, it3)
            } } }
        }

        cancelButton.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder
                .setTitle("DELETE/CANCEL Event?")
                .setMessage("Are you sure you want to DELETE your event?")
                .setPositiveButton("Remove") { dialog, which ->
                    Log.d("CANCEL CONFIRM", Obj.event.toString())

                    Obj.removeEvent(Obj.event.eventCode, object : Obj.eventDeleteListener {
                        override fun onEventDelete() {
                            val launchNextActivity: Intent = Intent(
                                this@ChangeTimeActivity,
                                MainActivity::class.java
                            )
                            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(launchNextActivity)
                            showToast("You have canceled this event")
                        }

                        override fun onCancelled(err: String) {
                            Toast.makeText(
                                applicationContext,
                                "An error has occured",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
                    dialog.dismiss()
                }
                .setNegativeButton("Nevermind") { dialog, which ->
                    dialog.cancel()
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        // Any initialization or setup code can go here
    }

    fun convertMilitaryToAMPM(militaryTime: String?): String {
        if (militaryTime==null)
            return ""
        val militaryFormat = SimpleDateFormat("HH:mm", Locale.US)
        val ampmFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val time = militaryFormat.parse(militaryTime)
        return ampmFormat.format(time)
    }

    override fun onDateSelected(selectedDate: String){
        date = selectedDate
        val availTimes = Obj.event.availability
        val pplCount = Obj.event.joined.size.toFloat()
        for ((index, timeslot) in timeslotAdapter.timeslots.withIndex()) {
            val datetime = "$date ${timeslot.time}"
            if (availTimes.containsKey(datetime)){
                val num = availTimes[datetime]?.size?.toFloat() ?: 0f
                timeslot.opacity = (num / pplCount * 0.8F) + 0.2F
            } else {
                timeslot.opacity = 0.2F
            }

            if (date == Obj.event.date && (timeslot.time == Obj.event.startTime || timeslot.time == Obj.event.endTime)) {
                // Simulate a click on the current time slot
                timeslotAdapter.notifyItemChanged(index)
                timeslotAdapter.timeslotClicked(index)
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