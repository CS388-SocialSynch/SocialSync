package com.cs388.socialsync

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val alarmButton: Button = findViewById(R.id.alarmButton);

        createNotificationChannel();

        alarmButton.setOnClickListener() {
            Toast.makeText(this, "Remainder Set", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ReminderBroadcast::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

            val timeAtButtonClick = System.currentTimeMillis()

            val tenSecondsInMillis = 1000 * 10 * 60 * 30

            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                timeAtButtonClick - tenSecondsInMillis,
                pendingIntent
            )

        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SocialSyncRemainderChannel";
            val description = "Event Notification!"
            val importance = NotificationManager.IMPORTANCE_DEFAULT;
            val channel = NotificationChannel("socialSyncNotif", name, importance);
            channel.description = description;

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)


        }
    }
}