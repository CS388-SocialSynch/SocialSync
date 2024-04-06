package com.cs388.socialsync

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ChangeTimeActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_time)

        val settingsButton = findViewById<Button>(R.id.changeSettingsButton)
        val setEventButton = findViewById<Button>(R.id.setTimeButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        val leftButton = findViewById<Button>(R.id.leftButton)
        val midLeftButton = findViewById<Button>(R.id.midLeftButton)
        val midButton = findViewById<Button>(R.id.midButton)
        val midRightButton = findViewById<Button>(R.id.midRightButton)
        val rightButton = findViewById<Button>(R.id.rightButton)

        val prevButton = findViewById<ImageButton>(R.id.prevButton)
        val nextButton = findViewById<ImageButton>(R.id.nextButton)
        val blackColor = ContextCompat.getColor(this, android.R.color.black)
        nextButton.setColorFilter(blackColor, PorterDuff.Mode.SRC_IN)
        prevButton.setColorFilter(blackColor,PorterDuff.Mode.SRC_IN)

        prevButton.setOnClickListener{
            showToast("Previous Date")
        }

        nextButton.setOnClickListener {
            showToast("Next Date")
        }
        leftButton.setOnClickListener {
            showToast("Monday")
        }

        midLeftButton.setOnClickListener {
            showToast("Tuesday")
        }

        settingsButton.setOnClickListener {
            showToast("Open Ethan's Activity")
        }

        setEventButton.isEnabled = false
        setEventButton.alpha = 0.5f


        cancelButton.setOnClickListener {
            showToast("You have canceled this event")
        }

        // Any initialization or setup code can go here
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}