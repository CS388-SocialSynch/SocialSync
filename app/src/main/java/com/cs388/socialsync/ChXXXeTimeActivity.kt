package com.cs388.socialsync

import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

open class ChXXXeTimeActivity: AppCompatActivity(), OnTimeslotSelectionListener, OnDateSelectionListener {


    protected var eventStarTime = ""
    protected var eventEndTime = ""
    protected var eventDates: List<String> = listOf()


    protected lateinit var dateAdapter: DateAdapter
    protected lateinit var toggleButton: Button
    protected var startTime: String? = null
    protected var endTime: String? = null
    protected var date: String? = null
    protected var times: List<String> = emptyList()


    protected fun enableButton(button: Button){
        button.setBackgroundResource(R.drawable.button_normal)
        button.setTextColor(ContextCompat.getColor(this, R.color.black))
        button.isEnabled = true
    }

    protected fun loadDBData(){
        Obj.fetchEventUsingCode(Obj.event.eventCode,object:Obj.SetOnEventFetchListener{
            override fun onEventFetch(event: Event) {
                Obj.event=event
            }
        })
        val tempStartTime = Obj.event.optionStartTime as String
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val militaryTime = LocalTime.parse(tempStartTime, formatter)
        eventStarTime = militaryTime.format(DateTimeFormatter.ofPattern("h:mm a"))

        val tempEndTime = Obj.event.optionEndTime as String
        val militaryTime2 = LocalTime.parse(tempEndTime, formatter)
        eventEndTime = militaryTime2.format(DateTimeFormatter.ofPattern("h:mm a"))

        //TODO: Add logic for days
        if(Obj.event.useSpecificDate){
            eventDates = Obj.event.optionalDates
        }else{
            eventDates = Obj.event.optionalDays
        }

    }

    protected fun disableButton(button: Button){
        button.setBackgroundResource(R.drawable.button_invalid)
        button.setTextColor(ContextCompat.getColor(this, R.color.deselected))
        button.isEnabled=false
    }

    override fun onDateSelected(selectedDate: String){
        date = selectedDate
        if(startTime!=null){
            enableButton(toggleButton)
        } else {disableButton(toggleButton)}
    }

    override fun onTimeslotsSelected(selectedTimeslots: List<String>) {

        if (selectedTimeslots.size == 2 ) {
            //showToast(selectedTimeslots[0]+" "+selectedTimeslots[1])
            if(date != null){
                enableButton(toggleButton)
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
        } else { disableButton(toggleButton) }
    }

    protected fun genDates(startDate: String, endDate: String): List<String> {
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

    protected fun showToast(message: String?) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show()
    }
}