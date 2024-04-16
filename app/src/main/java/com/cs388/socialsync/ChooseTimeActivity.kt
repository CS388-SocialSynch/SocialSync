package com.cs388.socialsync

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChooseTimeActivity: ChXXXeTimeActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_time)

        val showDates: Boolean = true //Get this from database

        val chooseDatesRecyclerView = findViewById<RecyclerView>(R.id.chooseDateRecyclerView)
        chooseDatesRecyclerView.layoutManager=
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        dateAdapter = DateAdapter(this, genDates("2024-03-28", "2024-04-05"), this, showDates)
        chooseDatesRecyclerView.adapter=dateAdapter

        val chooseTimeslotRecyclerView = findViewById<RecyclerView>(R.id.chooseTimeslotsRecyclerView)
        chooseTimeslotRecyclerView.layoutManager = LinearLayoutManager(this)
        timeslotAdapter = TimeslotAdapter(this, "8:00 AM", "11:00 AM",this)
        chooseTimeslotRecyclerView.adapter = timeslotAdapter

        toggleButton = findViewById<Button>(R.id.saveTimesButton)
        toggleButton.setOnClickListener {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd")
            val outputFormat = SimpleDateFormat("MM/dd")
            if (date!=null ) {
                val formattedDate = outputFormat.format(inputFormat.parse(date))
                showToast("Saved times for $formattedDate")
            }

        }
        val backButton: Button = findViewById<Button>(R.id.chooseTimeBackButton)
        backButton.setOnClickListener {
            finish()
        }
    }
}