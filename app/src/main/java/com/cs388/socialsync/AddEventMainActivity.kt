package com.cs388.socialsync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat

// include a dialog to discard
class AddEventMainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_event_settings)

        val eventNameEdit = findViewById<EditText>(R.id.eventName)
        val locationEdit = findViewById<EditText>(R.id.eventLocation)
        val publicSwitch = findViewById<SwitchCompat>(R.id.switchPublic)
        val inPerson = findViewById<SwitchCompat>(R.id.switchInPerson)
        val showParticipants = findViewById<SwitchCompat>(R.id.switchShowParticipants)
        val btnExit = findViewById<AppCompatButton>(R.id.btnExit)
        val btnNext = findViewById<AppCompatButton>(R.id.btnNext)
        val event = intent.getBundleExtra("eventInfo")?.getSerializable(EVENT_ITEM) as? Event

        Log.d("MAIN_ADD", event.toString())

        // Preloads information
        event?.let { details ->
            if (details.eventName != ""){
                eventNameEdit.setText(details.eventName)
            }
            if(details.locationName != "" && details.locationName != null) {
                locationEdit.setText(details.locationName.toString())
            }
                publicSwitch.isChecked = details.isPublic
                inPerson.isChecked = details.isInPerson
                showParticipants.isChecked=details.isInPerson

        }

        btnNext.setOnClickListener(){
            event?.let { details ->
                // TODO: add validation
                event.eventName = eventNameEdit.text.toString()
                event.locationName = locationEdit.text.toString()
                event.isPublic = publicSwitch.isChecked
                event.isInPerson = inPerson.isChecked
                event.isInPerson = showParticipants.isChecked
            }
            val intent = Intent(this,AddEventSelectDate::class.java)
            val bundle = Bundle()
            bundle.putSerializable(EVENT_ITEM, event)
            intent.putExtra("eventInfo",bundle)

            Toast.makeText(applicationContext, event?.startTime.toString(),Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }

//        Discard prompt / exit protocol
        btnExit.setOnClickListener(){
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
}