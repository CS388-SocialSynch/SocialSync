package com.cs388.socialsync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.core.widget.doAfterTextChanged
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class AddEventSelectDate:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_event_dates)


        val btnNext = findViewById<AppCompatButton>(R.id.btnNext)
        val btnBack = findViewById<AppCompatButton>(R.id.btnBack)
        val btnExit = findViewById<AppCompatButton>(R.id.btnExit)
        val btnMon = findViewById<AppCompatToggleButton>(R.id.MON)
        val btnTue = findViewById<AppCompatToggleButton>(R.id.TUE)
        val btnWed = findViewById<AppCompatToggleButton>(R.id.WED)
        val btnThu = findViewById<AppCompatToggleButton>(R.id.THU)
        val btnFri = findViewById<AppCompatToggleButton>(R.id.FRI)
        val btnSat = findViewById<AppCompatToggleButton>(R.id.SAT)
        val btnSun = findViewById<AppCompatToggleButton>(R.id.SUN)

        val btnChooseSpecificDate = findViewById<AppCompatButton>(R.id.btnSpecificDates)
        val startTimeEdit = findViewById<EditText>(R.id.startTime)
        val endTimeEdit = findViewById<EditText>(R.id.endTime)

        val timeMatch = Regex("^((0[0-9])|(1[0-2])):((00)|(15)|(30)|(45))\\s((AM)|(am)|(PM)|(pm))$")
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

        var startTimeCheck = false
        var endTimeCheck = false

        var start: LocalTime = LocalTime.MIDNIGHT
        var end: LocalTime = LocalTime.MAX

        val toggleBtns = arrayOf(btnMon,btnTue,btnWed,btnThu,btnFri,btnSat,btnSun)
        val fullWeek = arrayOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")


        // Preloading the events data
        var event = Obj.event
        event.let { details ->
            if (details.optionStartTime != null && details.optionStartTime != "") {
                startTimeEdit.setText(
                    LocalTime.parse(
                        details.optionStartTime,
                        DateTimeFormatter.ISO_LOCAL_TIME
                    ).format(timeFormatter).toString()
                )
                if (!timeMatch.containsMatchIn(startTimeEdit.text.toString())) {
                    startTimeEdit.setBackgroundDrawable(getDrawable(R.drawable.red_stroke))
                    startTimeCheck = false
                } else {
                    startTimeEdit.setBackgroundDrawable(getDrawable(R.drawable.green_stroke))
                    start = LocalTime.parse(startTimeEdit.text.toString(), timeFormatter)
                    startTimeCheck = true
                }
            }
            if (details.optionEndTime != null && details.optionEndTime != "") {
                endTimeEdit.setText(
                    LocalTime.parse(
                        details.optionEndTime,
                        DateTimeFormatter.ISO_LOCAL_TIME
                    ).format(timeFormatter).toString()
                )
                if (!timeMatch.containsMatchIn(endTimeEdit.text.toString())) {
                    endTimeEdit.setBackgroundDrawable(getDrawable(R.drawable.red_stroke))
                    endTimeCheck = false
                } else {
                    end = LocalTime.parse(endTimeEdit.text.toString(), timeFormatter)
                    if (end.compareTo(start) > 0) {
                        endTimeEdit.setBackgroundDrawable(getDrawable(R.drawable.green_stroke))
                        endTimeCheck = true
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Make sure end time ends after start",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


            if (details.useSpecificDate) {
                val temp = getString(R.string.choose_specfic_days) + " (selected)"
                btnChooseSpecificDate.setText(temp)
            }else if ( details.optionalDays.isNotEmpty() ){
                // TODO FIX THE LOAD IN DAYS
                details.optionalDays.forEach {
                    val btn = toggleBtns[fullWeek.indexOf(it)]
                    Log.d("OPTIONAL DAYS", btn.text.toString() + " " + fullWeek.indexOf(it))
                    btn.isChecked = true
                }
            }

        }

        // TODO DELETE *************
        Log.d("SELECT_DATE", event.toString())

//        check the value with an update and then make the add your time legal
        startTimeEdit.doAfterTextChanged {
            if (!timeMatch.containsMatchIn(startTimeEdit.text.toString())) {
                startTimeEdit.setBackgroundDrawable(getDrawable(R.drawable.red_stroke))
                startTimeCheck = false
            } else {
                startTimeEdit.setBackgroundDrawable(getDrawable(R.drawable.green_stroke))
                start = LocalTime.parse(startTimeEdit.text.toString(), timeFormatter)
                startTimeCheck = true
                event.optionStartTime = start.format(DateTimeFormatter.ISO_LOCAL_TIME)
            }
        }
        endTimeEdit.doAfterTextChanged {
            if (!timeMatch.containsMatchIn(endTimeEdit.text.toString())) {
                endTimeEdit.setBackgroundDrawable(getDrawable(R.drawable.red_stroke))
                endTimeCheck = false
            } else if (startTimeCheck) {
                end = LocalTime.parse(endTimeEdit.text.toString(), timeFormatter)
                if (end.compareTo(start) > 0) {
                    endTimeEdit.setBackgroundDrawable(getDrawable(R.drawable.green_stroke))
                    event.optionEndTime = end.format(DateTimeFormatter.ISO_LOCAL_TIME)
                    endTimeCheck = true
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Make sure end time ends after start",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else{
                Toast.makeText(
                    applicationContext,
                    "Make sure start time is valid",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnMon.setOnClickListener() {
            toggleButAction(btnMon, btnChooseSpecificDate, event)
        }
        btnTue.setOnClickListener() {
            toggleButAction(btnTue, btnChooseSpecificDate, event)
        }
        btnWed.setOnClickListener() {
            toggleButAction(btnWed, btnChooseSpecificDate, event)
        }
        btnThu.setOnClickListener() {
            toggleButAction(btnThu, btnChooseSpecificDate, event)
        }
        btnFri.setOnClickListener() {
            toggleButAction(btnFri, btnChooseSpecificDate, event)
        }
        btnSat.setOnClickListener() {
            toggleButAction(btnSat, btnChooseSpecificDate, event)
        }
        btnSun.setOnClickListener() {
            toggleButAction(btnSun, btnChooseSpecificDate, event)
        }

        btnChooseSpecificDate.setOnClickListener() {
            event.optionalDays.clear()
            btnMon.isChecked = false
            btnMon.setBackgroundDrawable(getDrawable(R.drawable.button_stroke))
            btnTue.isChecked = false
            btnTue.setBackgroundDrawable(getDrawable(R.drawable.button_stroke))
            btnWed.isChecked = false
            btnWed.setBackgroundDrawable(getDrawable(R.drawable.button_stroke))
            btnThu.isChecked = false
            btnThu.setBackgroundDrawable(getDrawable(R.drawable.button_stroke))
            btnFri.isChecked = false
            btnFri.setBackgroundDrawable(getDrawable(R.drawable.button_stroke))
            btnSat.isChecked = false
            btnSat.setBackgroundDrawable(getDrawable(R.drawable.button_stroke))
            btnSun.isChecked = false
            btnSun.setBackgroundDrawable(getDrawable(R.drawable.button_stroke))

            if (startTimeCheck && endTimeCheck) {
                val nextIntent = Intent(this@AddEventSelectDate, AddEventChooseDates::class.java)
                startActivity(nextIntent)
            } else {
                startTimeEdit.setBackgroundDrawable(getDrawable(R.drawable.red_stroke))
                endTimeEdit.setBackgroundDrawable(getDrawable(R.drawable.red_stroke))
                Toast.makeText(
                    applicationContext,
                    "Insert start and end time first",
                    Toast.LENGTH_SHORT
                ).show()
            }

            if(event.useSpecificDate){
                val temp = getString(R.string.choose_specfic_days) + " (selected)"
                btnChooseSpecificDate.setText(temp)
            }
        }

        btnNext.setOnClickListener() {
            // validation
            if ((event.useSpecificDate || event.optionalDays.isNotEmpty()) && startTimeCheck && endTimeCheck) {

                // next step
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder
                    .setTitle("Submit Event?")
                    .setMessage("Are you sure you want to submit?")
                    .setPositiveButton("Submit") { dialog, which ->
                        //When submit
                        val launchNextActivity =
                            Intent(this@AddEventSelectDate, AddEventFinished::class.java)
                        startActivity(launchNextActivity)
                        finish()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.cancel()
                    }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Please select days/dates and enter times",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        btnBack.setOnClickListener() {
            val launchNextActivity: Intent = Intent(
                this@AddEventSelectDate,
                AddEventMainActivity::class.java
            )
            startActivity(launchNextActivity)
            finish()
        }

        // Discard prompt / exit protocol
        btnExit.setOnClickListener() {
            discardView()
        }

    }

    private fun toggleButAction(
        btn: AppCompatToggleButton,
        btnSpecificDate: AppCompatButton,
        event: Event?
    ) {
        if (event != null) {

            if (btn.isChecked) {
                btn.setBackgroundDrawable(getDrawable(R.drawable.button_normal))
                event.optionalDays.add(btn.textOn.toString())
            } else {
                btn.setBackgroundDrawable(getDrawable(R.drawable.button_stroke))
                event.optionalDays.remove(btn.textOn.toString())
                if (event.optionalDays.isEmpty()) {
                    event.useSpecificDate = false
                }
            }

            if (event.useSpecificDate) {
                btnSpecificDate.setText(R.string.choose_specfic_days)
                event.useSpecificDate = false
            }
        } else {
            Toast.makeText(applicationContext, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun discardView() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle("Discard Event?")
            .setMessage("Are you sure you want to discard your work?")
            .setPositiveButton("Discard") { dialog, which ->
                val launchNextActivity: Intent = Intent(
                    this@AddEventSelectDate,
                    MainActivity::class.java
                )
                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(launchNextActivity)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun updateSpecificDates(){

    }
}