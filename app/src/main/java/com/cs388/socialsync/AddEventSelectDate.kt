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
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
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


        val event = intent.getBundleExtra("eventInfo")?.getSerializable(EVENT_ITEM) as? Event
        event.let { details->

            if (details?.startTime != null) {
                startTimeEdit.setText(details.optionStartTime!!.format(timeFormatter).toString())
            }
            if (details?.endTime != null) {
                startTimeEdit.setText(details.optionEndTime!!.format(timeFormatter).toString())
            }
        }

        Log.d("SELECT_DATE", event.toString())

//        check the value with an update and then make the add your time legal
        startTimeEdit.doAfterTextChanged {
            if (!timeMatch.containsMatchIn(startTimeEdit.text.toString())) {
                startTimeEdit.setBackgroundDrawable(getDrawable(R.drawable.red_stroke))
                startTimeCheck = false
            } else {
                startTimeEdit.setBackgroundDrawable(getDrawable(R.drawable.green_stroke))
                event?.optionStartTime = LocalTime.parse(startTimeEdit.text.toString(),timeFormatter)
                startTimeCheck = true
            }
        }
        endTimeEdit.doAfterTextChanged {
            if (!timeMatch.containsMatchIn(endTimeEdit.text.toString())) {
                endTimeEdit.setBackgroundDrawable(getDrawable(R.drawable.red_stroke))
                endTimeCheck = false
            } else {
                val endTimeTemp = LocalTime.parse(endTimeEdit.text.toString(),timeFormatter)
                if(endTimeTemp.compareTo(event?.optionStartTime) > 0) {
                    endTimeEdit.setBackgroundDrawable(getDrawable(R.drawable.green_stroke))
                    endTimeCheck = true
                }else{
                    Toast.makeText(applicationContext,"Make sure end time ends after start",Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnMon.setOnClickListener(){
            toggleButAction(btnMon, event)
        }
        btnTue.setOnClickListener(){
            toggleButAction(btnTue, event)
        }
        btnWed.setOnClickListener(){
            toggleButAction(btnWed, event)
        }
        btnThu.setOnClickListener(){
            toggleButAction(btnThu, event)
        }
        btnFri.setOnClickListener(){
            toggleButAction(btnFri, event)
        }
        btnSat.setOnClickListener(){
            toggleButAction(btnSat, event)
        }
        btnSun.setOnClickListener(){
            toggleButAction(btnSun, event)
        }


        btnNext.setOnClickListener(){
            //TODO set the ability to use next if everything is correct
            // check if optionalDays or check if specificDate set
        }

        btnBack.setOnClickListener(){
            val launchNextActivity: Intent = Intent(
                this@AddEventSelectDate,
                AddEventMainActivity::class.java
            )
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            val bundle = Bundle()
            bundle.putSerializable(EVENT_ITEM, event)
            launchNextActivity.putExtra("eventInfo",bundle)
            startActivity(launchNextActivity)
            finish()
        }

        //        Discard prompt / exit protocol
        btnExit.setOnClickListener(){
            discardView()
        }

    }

    private fun toggleButAction(btn: AppCompatToggleButton, event:Event?) {
        if (event != null) {
            if (btn.isChecked) {
                btn.setBackgroundDrawable(getDrawable(R.drawable.button_normal))
                event.optionalDays.add(btn.textOn.toString())
            } else {
                btn.setBackgroundDrawable(getDrawable(R.drawable.button_stroke))
                event.optionalDays.remove(btn.textOn.toString())
                if (event.optionalDays.isEmpty()){
                    event.specificDate= false
                }
            }
//            Log.d("thing", event.optionalDays.toString())
        }
        else{
            Toast.makeText(applicationContext, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun discardView(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle("Discard Event?")
            .setMessage("Are you sure you want to discard your work?")
            .setPositiveButton("Discard") { dialog, which ->
                finish()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}