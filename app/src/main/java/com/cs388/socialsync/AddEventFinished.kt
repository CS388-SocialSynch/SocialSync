package com.cs388.socialsync

import android.R.attr.label
import android.R.attr.text
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton


class AddEventFinished : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_event_end)

        val code = findViewById<TextView>(R.id.code)
        val btnFinish = findViewById<AppCompatButton>(R.id.btnFinish)
        val event = Obj.event

        if(Obj.updateEvent == ""){
            val host = Obj.auth.currentUser!!.uid
            event.hostUID = host
            event.joined.add(host)

            //TODO DELETE THIS **********
            Log.d("EVENT CREATE", event.toString())

                val listener = object : Obj.SetOnDuplicateEventCheckListener {
                    override fun onDuplicateEvent() {
                        Toast.makeText(
                            this@AddEventFinished,
                            "Please change event name",
                            Toast.LENGTH_SHORT
                        ).show()
                        val nextIntent= Intent(this@AddEventFinished, AddEventMainActivity::class.java)
                        startActivity(nextIntent)
                    }

                    override fun onEventAdded(key: String) {
                        Toast.makeText(
                            this@AddEventFinished,
                            "Event added to Database and key copied",
                            Toast.LENGTH_SHORT
                        ).show()
                        code.setText(code.text.toString() + " " + key)
                        var myClipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip:ClipData = ClipData.newPlainText("Event Code", key)
                        event.eventCode = key
                        Obj.addEventToUser(event.eventCode)
                    }
                }
                Obj.addEventToDatabase(event, listener, false,true)

        }else{
            Log.d("UPDATE EVENT", Obj.updateEvent.toString() + " " + event.eventCode)
            Obj.updateEventOnDatabase(event, event.eventCode, object: Obj.SetOnDuplicateEventCheckListener{
                override fun onDuplicateEvent() {
                    Toast.makeText(
                        this@AddEventFinished,
                        "Please change event name",
                        Toast.LENGTH_SHORT
                    ).show()
                    val nextIntent= Intent(this@AddEventFinished, AddEventMainActivity::class.java)
                    startActivity(nextIntent)
                }

                override fun onEventAdded(key: String) {
                    Toast.makeText(
                        this@AddEventFinished,
                        "Event added to Database", //and key copied",
                        Toast.LENGTH_SHORT
                    ).show()
                    code.setText(code.text.toString() + " " + key)
    //                var myClipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    //                val clip:ClipData = ClipData.newPlainText("Event Code", key)
                    event.eventCode = key
                }
            })
        }

        btnFinish.setOnClickListener() {
            val launchNextActivity: Intent = Intent(
                this@AddEventFinished,
                MainActivity::class.java
            )
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(launchNextActivity)
        }

    }
}