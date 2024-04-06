package com.cs388.socialsync

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
class ChangeTimeActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_time)

        val settingsButton = findViewById<Button>(R.id.changeSettingsButton)
        val setEventButton = findViewById<Button>(R.id.setTimeButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        settingsButton.setOnClickListener {
            showToast("Open Ethan's Activity")
        }

        setEventButton.isEnabled = false

        cancelButton.setOnClickListener {
            showToast("You have canceled this event")
        }

        // Any initialization or setup code can go here
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}