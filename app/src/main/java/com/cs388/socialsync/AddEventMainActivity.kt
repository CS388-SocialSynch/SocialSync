package com.cs388.socialsync

import android.os.Bundle
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