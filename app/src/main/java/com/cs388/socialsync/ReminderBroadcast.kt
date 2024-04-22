package com.cs388.socialsync

import android.Manifest
import android.content.BroadcastReceiver;
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

public class ReminderBroadcast() : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {

        var state = "Test"
        val action = intent!!.action
//        Log.e("CUSTOM---->", intent.action.toString())
//        if (action == "com.cs388.socialsync") {
            state = intent.getStringExtra("msg").toString()
//        }
//        state = intent.extras!!.getString("msg").toString()
        Log.e("CUSTOM----> d", intent.hasExtra("msg").toString() + "==")
        Log.e("CUSTOM----> d", intent.extras!!.getString("test").toString() + "=+=")


        val builder = NotificationCompat.Builder(context!!, "socialSyncNotif")
            .setSmallIcon(R.drawable.cloudy_icon)
            .setContentTitle(state)
            .setContentText("Event today!!! Dont Forget")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        val notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val rand = (Math.random() * 1000).toInt()
        Log.e("CUSTOM----> Random", rand.toString())
        notificationManager.notify(rand, builder.build())


    }
}
