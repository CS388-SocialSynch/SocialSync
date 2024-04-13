package com.cs388.socialsync

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object Obj {

    lateinit var USER_DB: DatabaseReference
    lateinit var USERS_DB: DatabaseReference
    lateinit var EVENTS_DB: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var user: User
    var eventList: MutableList<Event> = mutableListOf()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    fun uploadUserData(user: User) {
        with(USER_DB) {
            child("displayName").setValue(user.displayName)
            child("email").setValue(user.email)
            child("image").setValue(user.image)
        }
    }

    fun getUserData(listener: UserDataListener) {
        val userDataListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = createUserFromSnapshot(dataSnapshot)
                loadEvents(user, listener)
                USER_DB.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                // i guess we toast an error or something
            }
        }

        USER_DB.addValueEventListener(userDataListener)
    }

    private fun createUserFromSnapshot(dataSnapshot: DataSnapshot): User {
        val displayName = dataSnapshot.child("displayName").value.toString()
        val email = dataSnapshot.child("email").value.toString()
        val image = dataSnapshot.child("image").value.toString()
        val events = dataSnapshot.child("events").children.map { it.value.toString() }
        return User(displayName, email, image, events)
    }

    private fun loadEvents(user: User, listener: UserDataListener) {
        user.events.forEach { uid ->
            EVENTS_DB.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(eventSnapshot: DataSnapshot) {
                    val event = createEventFromSnapshot(eventSnapshot)
                    eventList.add(event)
                    updateEventParticipants(event, eventSnapshot)
                    Log.d("Event", event.toString())
                    listener.onUserDataLoad(user)
                }

                override fun onCancelled(error: DatabaseError) {
                    // i guess we toast an error or something
                }
            })
        }
    }

    private fun updateEventParticipants(event: Event, eventSnapshot: DataSnapshot) {
        val participantsData = eventSnapshot.child("participants").value
        if (participantsData is List<*>) {
            participantsData.forEach { participant ->
                if (participant is Map<*, *>) {
                    val participantUID = participant.keys.firstOrNull() as? String
                    val isAttending = participant.values.firstOrNull() as? Boolean
                    if (participantUID != null && isAttending != null) {
                        fetchParticipantData(participantUID) { userName ->
                            event.participants.add(User(userName, isAttending))
                        }
                    }
                }
            }
        }
    }


    private fun fetchParticipantData(uid: String, callback: (String) -> Unit) {
        USERS_DB.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {
                val userName = userSnapshot.child("displayName").value.toString()
                callback(userName)
            }

            override fun onCancelled(error: DatabaseError) {
                // i guess we toast an error or something
            }
        })
    }

    private fun createEventFromSnapshot(eventSnapshot: DataSnapshot): Event {
        return Event(
            eventSnapshot.key.toString(),
            eventSnapshot.child("eventName").value.toString(),
            LocalTime.parse(eventSnapshot.child("startTime").value.toString(), formatter),
            LocalTime.parse(eventSnapshot.child("endTime").value.toString(), formatter),
            LocalDate.parse(eventSnapshot.child("date").value.toString()),
            eventSnapshot.child("temperature").value as Int?,
            eventSnapshot.child("weatherCondition").value?.toString(),
            eventSnapshot.child("locationName").value?.toString(),
            eventSnapshot.child("address").value.toString(),
            eventSnapshot.child("isHost").value as Boolean,
            eventSnapshot.child("isPublic").value as Boolean,
            eventSnapshot.child("showParticipants").value as Boolean,
            1234,
            mutableListOf(),
            mutableListOf() //empty list for now, we set it up in the update participants above
        )
    }

    interface UserDataListener {
        fun onUserDataLoad(user: User)
    }

    data class User(
        var displayName: String,
        var email: String,
        var image: String,
        var events: List<String>
    )

}
