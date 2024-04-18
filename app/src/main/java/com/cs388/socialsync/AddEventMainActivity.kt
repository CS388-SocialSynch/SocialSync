package com.cs388.socialsync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doAfterTextChanged
import java.time.LocalTime

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

        val streetEdit = findViewById<EditText>(R.id.eventStreet)
        val townEdit = findViewById<EditText>(R.id.eventTown)
        val stateEdit = findViewById<EditText>(R.id.eventState)
        val countryEdit = findViewById<EditText>(R.id.eventCountry)
        val zipEdit = findViewById<EditText>(R.id.eventZip)

        val addressSnippet = findViewById<LinearLayout>(R.id.addressSnippet)

        val event = Obj.event
        var validationCheck = false
        var addressCheck = true
        var zipCheck = false


        // TODO DELETE *************
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
                showParticipants.isChecked=details.showParticipants

            if (details.isInPerson){
                inPerson.isChecked = details.isInPerson
                addressSnippet.visibility = View.VISIBLE
            }

            if(details.addressStreet!= ""){
                streetEdit.setText(details.addressStreet)
            }
            if(details.addressTown!= ""){
                townEdit.setText(details.addressTown)
            }
            if(details.addressState!= ""){
                stateEdit.setText(details.addressState)
                validateText(stateEdit,Regex("^\\w\\w$"))
            }
            if(details.addressCountry!= ""){
                countryEdit.setText(details.addressCountry)
            }
            if(details.addressZipCode!= ""){
                zipEdit.setText(details.addressZipCode)
            }
        }

        // Validate event name filled out
        eventNameEdit.doAfterTextChanged {
            if (eventNameEdit.text.toString() == "") {
                eventNameEdit.setBackgroundDrawable(getDrawable(R.drawable.red_stroke))
                validationCheck = false
            } else {
                eventNameEdit.setBackgroundDrawable(getDrawable(R.drawable.green_stroke))
                validationCheck = true
            }
        }

        stateEdit.doAfterTextChanged {
            addressCheck = validateText(stateEdit,Regex("^\\w\\w$"))
        }

        zipEdit.doAfterTextChanged {
            zipCheck = validateText(zipEdit,Regex("^\\d{5}$"))
        }

        inPerson.setOnClickListener(){
            if(inPerson.isChecked){
                addressSnippet.visibility = View.VISIBLE
                event?.isInPerson = true
            }
            else{
                addressSnippet.visibility = View.INVISIBLE
                event?.isInPerson = false
            }
        }


        btnNext.setOnClickListener(){
            event.let { details ->
                event.eventName = eventNameEdit.text.toString()
                event.locationName = locationEdit.text.toString()
                event.isPublic = publicSwitch.isChecked
                event.isInPerson = inPerson.isChecked
                event.showParticipants = showParticipants.isChecked

                if(event.isInPerson){
                    if(addressCheck && zipCheck && streetEdit.text.toString() != "" && townEdit.text.toString() != "" && stateEdit.text.toString() != ""  && countryEdit.text.toString() != ""){
                        addressCheck= true
                        event.addressStreet = streetEdit.text.toString()
                        event.addressTown = townEdit.text.toString()
                        event.addressState = stateEdit.text.toString()
                        event.addressCountry = countryEdit.text.toString()
                        event.addressZipCode = zipEdit.text.toString()
                    }else{
                        Toast.makeText(applicationContext,"Please fill out address", Toast.LENGTH_SHORT).show()
                        addressCheck = false
                    }
                }else{
                    addressCheck = true
                }

                // Validation checks
                if(event.locationName == ""){
                    validationCheck=false
                    Toast.makeText(applicationContext,"Please enter specific location/URL", Toast.LENGTH_SHORT).show()
                }else {
                    validationCheck=true
                }
            }

            if (validationCheck && addressCheck) {
                val intent = Intent(this, AddEventSelectDate::class.java)
//                val bundle = Bundle()
//                bundle.putSerializable(EVENT_ITEM, event)
//                intent.putExtra("eventInfo", bundle)
                startActivity(intent)
                finish()
            }
        }

//        Discard prompt / exit protocol
        btnExit.setOnClickListener(){
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder
                .setTitle("Discard Event?")
                .setMessage("Are you sure you want to discard your work?")
                .setPositiveButton("Discard") { dialog, which ->
                    val launchNextActivity: Intent = Intent(
                        this@AddEventMainActivity,
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

    }

    private fun validateText(editText: EditText, regex: Regex): Boolean{
        if (!regex.containsMatchIn(editText.text.toString())) {
            editText.setBackgroundDrawable(getDrawable(R.drawable.red_stroke))
            return false
        } else {
            editText.setBackgroundDrawable(getDrawable(R.drawable.green_stroke))
            return true
        }
    }
}