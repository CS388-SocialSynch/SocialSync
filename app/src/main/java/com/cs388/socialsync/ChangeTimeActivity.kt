package com.cs388.socialsync

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class ChangeTimeActivity: AppCompatActivity() , OnTimeslotSelectionListener, OnDateSelectionListener {

    private lateinit var timeslotAdapter: TimeslotAdapter
    private lateinit var dateAdapter: DateAdapter
    private lateinit var setEventButton: Button
    private var startTime: String? = null
    private var endTime: String? = null
    private var date: String? = null
    private var showDates: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_time)

        val settingsButton = findViewById<Button>(R.id.changeSettingsButton)
        setEventButton = findViewById<Button>(R.id.setTimeButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val propTextView = findViewById<TextView>(R.id.eventPropertiesTextView)

        /*Removed Next Button
        val prevButton = findViewById<ImageButton>(R.id.prevButton)
        val nextButton = findViewById<ImageButton>(R.id.nextButton)
        val blackColor = ContextCompat.getColor(this, android.R.color.black)
        nextButton.setColorFilter(blackColor, PorterDuff.Mode.SRC_IN)
        prevButton.setColorFilter(blackColor,PorterDuff.Mode.SRC_IN)
        */
        val datesRecyclerView = findViewById<RecyclerView>(R.id.dateRecyclerView)
        datesRecyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        dateAdapter = DateAdapter(this, genDates("2024-03-28", "2024-04-05"), this, showDates)
        datesRecyclerView.adapter=dateAdapter

        val timeslotRecyclerView = findViewById<RecyclerView>(R.id.timeslotsRecyclerView)
        timeslotRecyclerView.layoutManager = LinearLayoutManager(this)
        timeslotAdapter = TimeslotAdapter(this, "8:00 AM", "11:00 AM",this)
        timeslotRecyclerView.adapter = timeslotAdapter



        settingsButton.setOnClickListener {
            showToast("Open Ethan's Activity")
        }


        //setEventButton.isEnabled = false
        //setEventButton.alpha = 0.5f
        setEventButton.setOnClickListener {
            if(showDates)
                propTextView.text = "Date:$date\nStart Time: $startTime\nEnd Time: $endTime \nJoin CODE: ---"
            else {
                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val currDate = LocalDate.parse(date, inputFormatter)
                val dayOfWeek = currDate.dayOfWeek.toString().lowercase().capitalize()
                propTextView.text = "Date:$dayOfWeek\nStart Time: $startTime\nEnd Time: $endTime \nJoin CODE: ---"
            }
        }



        cancelButton.setOnClickListener {
            showToast("You have canceled this event")
        }

        // Any initialization or setup code can go here
    }

    private fun enableEventButton(){
            setEventButton.setBackgroundResource(R.drawable.button_normal)
            setEventButton.setTextColor(ContextCompat.getColor(this, R.color.black))
            setEventButton.isEnabled = true
    }

   private fun disableEventButton(){
        setEventButton.setBackgroundResource(R.drawable.button_invalid)
        setEventButton.setTextColor(ContextCompat.getColor(this, R.color.deselected))
        setEventButton.isEnabled=false
   }

    override fun onDateSelected(selectedDate: String){
        date = selectedDate
        if(startTime!=null){
            enableEventButton()
        } else {disableEventButton()}
    }

    override fun onTimeslotsSelected(selectedTimeslots: MutableList<String>) {

        if (selectedTimeslots.size == 2) {
            //showToast(selectedTimeslots[0]+" "+selectedTimeslots[1])
            if(date != null){
                enableEventButton()
            }

            val format = SimpleDateFormat("h:mm a")
            val time1 = format.parse(selectedTimeslots[0])
            val time2 = format.parse(selectedTimeslots[1])

            if(  time1.before(time2) ){
                startTime = selectedTimeslots[0]
                endTime = selectedTimeslots[1]
            } else {
                startTime = selectedTimeslots[1]
                endTime = selectedTimeslots[0]
            }
        } else { disableEventButton() }

    }

    private fun genDates(startDate: String, endDate: String): List<String> {
        val dateList = mutableListOf<String>()

        // Parse the start and end dates
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        var currentDate = LocalDate.parse(startDate, formatter)
        val finalDate = LocalDate.parse(endDate, formatter)

        if (finalDate.isBefore(currentDate)) {
            return dateList
        }

        while (!currentDate.isAfter(finalDate)) {
            dateList.add(currentDate.format(formatter))
            currentDate = currentDate.plusDays(1)
        }

        return dateList
    }

    private fun showToast(message: String?) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show()
    }
}