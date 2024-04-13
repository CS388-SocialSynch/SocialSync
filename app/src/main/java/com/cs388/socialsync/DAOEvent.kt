package com.cs388.socialsync

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.format.DateTimeFormatter


class DAOEvent {
    lateinit var dataBaseRef:DatabaseReference

//        val db = FirebaseDatabase.getInstance()
//        dataBaseRef = db.getReference("EVENTS")

    public fun add(e:Event) {
        if (e == null)
            return

        //todo figure out how to get the current logged in UID and make a new UID for the event
        // return the new eventUID for the next page

        val userID = Obj.auth.currentUser!!.uid.toString()
        e.hostUID = userID
        e.participants.add(userID)


        // Users
        dataBaseRef.child("hostID").setValue(e.hostUID)
        dataBaseRef.child("participants").setValue(e.participants)

        dataBaseRef.child("eventName").setValue(e.eventName)
        // Final chosen times
        dataBaseRef.child("startTime")
            .setValue(e.startTime?.format(DateTimeFormatter.ISO_OFFSET_TIME))
        dataBaseRef.child("endTime").setValue(e.endTime?.format(DateTimeFormatter.ISO_OFFSET_TIME))
        dataBaseRef.child("date").setValue(e.date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        // Locations
        dataBaseRef.child("location").setValue(e.locationName)
        dataBaseRef.child("address").child("street").setValue(e.addressStreet)
        dataBaseRef.child("address").child("town").setValue(e.addressTown)
        dataBaseRef.child("address").child("state").setValue(e.addressState)
        dataBaseRef.child("address").child("country").setValue(e.addressCountry)
        // Settings
        dataBaseRef.child("isPublic").setValue(e.isPublic)
        dataBaseRef.child("showParticipants").setValue(e.showParticipants)
        dataBaseRef.child("isInPerson").setValue(e.isInPerson)
        dataBaseRef.child("useSpecificDates").setValue(e.useSpecificDate)
        dataBaseRef.child("isAPI").setValue(e.isAPI)
        // Options
        dataBaseRef.child("optionStartTime").setValue(e.optionStartTime)
        dataBaseRef.child("optionEndTime").setValue(e.optionEndTime)
        dataBaseRef.child("optionalDates").setValue(e.optionalDates)
        dataBaseRef.child("optionalDays").setValue(e.optionalDays)

    }

    private fun updateCount(): Int{
        val x = dataBaseRef.child("curr").get() as Int + 1
        // TODO convert to int and then update then pass back for event code / eventUID
        dataBaseRef.child("curr").setValue(x)
        return x

        Log.d("Test", x.toString())
    }

    public fun updateEvent(key:String, event: Event?){

    }
    public fun removeEvent(key:String){
    }

}
