package com.cs388.socialsync

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChooseTimeActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_time)
        val saveButton = findViewById<Button>(R.id.saveTimesButton)
        saveButton.setOnClickListener {
            showToast("Saved?")
        }

    }

    private fun showToast(message: String?) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show()
    }
}