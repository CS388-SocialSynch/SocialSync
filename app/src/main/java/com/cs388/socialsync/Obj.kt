package com.cs388.socialsync

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import java.time.format.DateTimeFormatter
import java.util.Random
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


object Obj {

    lateinit var USER_DB: DatabaseReference
    lateinit var USERS_DB: DatabaseReference
    lateinit var EVENTS_DB: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var user: User
    lateinit var loggedUserID: String
    var eventList: MutableList<Event> = mutableListOf()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    lateinit var event: Event
    var updateEvent: String = ""
    var updateEventOldName: String = ""

    interface SetOnEventFetchListener {
        fun onEventFetch(event: Event)
    }

    fun fetchEventUsingCode(code: String, listener: SetOnEventFetchListener) {
        val eventDb = EVENTS_DB.child(code)
        val dbListener = object : ValueEventListener {
            override fun onDataChange(eventSnapshot: DataSnapshot) {
                val event = createEventFromSnapshot(eventSnapshot)
                listener.onEventFetch(event)
                eventDb.removeEventListener(this)

            }

            override fun onCancelled(error: DatabaseError) {
                // i guess we toast an error or something
            }
        }
        eventDb.addListenerForSingleValueEvent(dbListener)
    }


    fun uploadUserData(user: User) {
        USER_DB.child("displayName").setValue(user.displayName)
        USER_DB.child("email").setValue(user.email)
        USER_DB.child("image").setValue(user.image)
    }

    fun getUserData(listener: UserDataListener) {
        Log.e("CUSTOM---->", "getUserData")

        val userDataListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.e("CUSTOM---->", "onDataChange")
                val user = createUserFromSnapshot(dataSnapshot)
                USER_DB.removeEventListener(this)
                listener.onUserDataLoad(user)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CUSTOM---->", "ERROR_" + error.message)
            }
        }

        USER_DB.addValueEventListener(userDataListener)
    }

    private fun createUserFromSnapshot(dataSnapshot: DataSnapshot): User {
        val displayName = dataSnapshot.child("displayName").value.toString()
        val email = dataSnapshot.child("email").value.toString()
        val image = dataSnapshot.child("image").value.toString()

        val events = mutableListOf<String>()
        for (eventId in dataSnapshot.child("events").children) {
            events.add(eventId.value.toString())
        }

        return User(displayName, email, image, events)
    }

    interface SetOnLoadEventListener {
        fun onDataLoad()
    }

    fun loadEvents(listener: SetOnLoadEventListener) {
        eventList.clear()
        Log.e("CUSTOM---->", "loadEvents")

        if (user.events.isEmpty()) {
            listener.onDataLoad()
            return
        }

        var count = 0
        Log.d("All user events in the user object", user.events.toString())
        user.events.forEach { uid ->
            val aaaa = EVENTS_DB.child(uid)

            val lis = object : ValueEventListener {
                override fun onDataChange(eventSnapshot: DataSnapshot) {

                    val event = createEventFromSnapshot(eventSnapshot)
                    eventList.add(event)
                    //updateEventParticipants(event, eventSnapshot)
                    Log.d("Event", event.toString())

                    aaaa.removeEventListener(this)
                    count++
                    if (count == user.events.size) {
                        listener.onDataLoad()
                    }
                    Log.e("CUSTOM---->", "forEach")
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled event
                }
            }
            aaaa.addListenerForSingleValueEvent(lis)
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
                        event.participants.add(participantUID)
                    }
                }
            }
        }
    }

    fun fetchParticipantData(uid: String, callback: (String) -> Unit) {
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

    fun removeUserFromEventAndParticipants(event: Event, userID: String) {
        removeEventFromUser(event)
        removeUserFromArray(EVENTS_DB.child(event.eventCode).child("participants"), userID) {
            println("User removed from participants array.")
        }
        removeUserFromArray(EVENTS_DB.child(event.eventCode).child("joined"), userID) {
            println("User removed from joined array.")
        }
    }

    private fun removeEventFromUser(event: Event) {
        val eventsRef = USER_DB.child("events")
        eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val events = dataSnapshot.getValue(object : GenericTypeIndicator<List<String>>() {})
                events?.let {
                    val updatedEvents = it.filter { eventId -> eventId != event.eventCode }
                    eventsRef.setValue(updatedEvents)
                        .addOnSuccessListener {
                            println("UID removed from user's events array.")
                        }
                        .addOnFailureListener { e ->
                            println("Error removing UID from user's events array: $e")
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error if needed
            }
        })
    }

    private fun removeUserFromArray(ref: DatabaseReference, userID: String, onSuccess: () -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val array = dataSnapshot.getValue(object : GenericTypeIndicator<List<String>>() {})
                array?.let {
                    val updatedArray = it.filter { item -> item != userID }
                    ref.setValue(updatedArray)
                        .addOnSuccessListener {
                            onSuccess.invoke()
                        }
                        .addOnFailureListener { e ->
                            println("Error removing user from array: $e")
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error if needed
            }
        })
    }

    fun updateUserAttendanceInEvent(eventCode: String, userID: String, addParticipant: Boolean) {
        val eventParticipantsRef = EVENTS_DB.child(eventCode).child("participants")
        eventParticipantsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val participants =
                    dataSnapshot.getValue(object : GenericTypeIndicator<List<String>>() {})
                participants?.let {
                    val updatedParticipants = it.toMutableList()
                    if (addParticipant) {
                        updatedParticipants.add(userID)
                        println("User added to participants array.")
                    } else {
                        updatedParticipants.remove(userID)
                        println("User removed from participants array.")
                    }
                    eventParticipantsRef.setValue(updatedParticipants)
                        .addOnSuccessListener {
                            println("Updated participants array in database.")
                        }
                        .addOnFailureListener { e ->
                            println("Error updating participants array: $e")
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error if needed
            }
        })
    }

    private fun createEventFromSnapshot(eventSnapshot: DataSnapshot): Event {

        val optionalDatesList = mutableListOf<String>()
        for (optionalDates in eventSnapshot.child("optionalDates").children) {
            optionalDatesList.add(optionalDates.value.toString())
        }

        val optionalDayList = mutableListOf<String>()
        for (optionalDays in eventSnapshot.child("optionalDays").children) {
            optionalDayList.add(optionalDays.value.toString())
        }

        val participantsList = mutableListOf<String>()
        for (participants in eventSnapshot.child("participants").children) {
            participantsList.add(participants.value.toString())
        }

        val joinedList = mutableListOf<String>()
        for (joined in eventSnapshot.child("joined").children) {
            joinedList.add(joined.value.toString())
        }

        for (aa in eventSnapshot.children) {
            Log.e("CUSTOM====>", aa.key.toString() + "     " + aa.value)

        }

        val event = Event(
            eventSnapshot.child("eventName").value.toString(),
            eventSnapshot.child("startTime").value.toString(),
            eventSnapshot.child("endTime").value.toString(),
            eventSnapshot.child("date").value.toString(),
            0,
            "",
            0,
            0,
            0,
            eventSnapshot.child("locationName").value?.toString(),
            eventSnapshot.child("address").value.toString(),
            eventSnapshot.child("public").value as Boolean,
            eventSnapshot.child("showParticipants").value as Boolean,
            eventSnapshot.child("inPerson").value as Boolean,
            eventSnapshot.child("hostUID").value.toString(),
            eventSnapshot.child("optionStartTime").value.toString(),
            eventSnapshot.child("optionEndTime").value.toString(),
            optionalDatesList,
            optionalDayList,
            eventSnapshot.child("useSpecificDate").value as Boolean,
            eventSnapshot.child("addressStreet").value.toString(),
            eventSnapshot.child("addressTown").value.toString(),
            eventSnapshot.child("addressState").value.toString(),
            eventSnapshot.child("addressCountry").value.toString(),
            eventSnapshot.child("addressZipcode").value.toString(),
            eventSnapshot.child("api").value as Boolean,
            joinedList,
            participantsList,
            eventSnapshot.child("eventCode").value.toString()
        )


        return event
    }

    interface SetOnDuplicateEventCheckListener {
        fun onDuplicateEvent()

        fun onEventAdded(key: String)
    }

    fun addEventToUser(key: String) {
        // Check if the event is not already in the user's events list
        if (!user.events.contains(key)) {
            user.events.add(key)
            USER_DB.child("events").setValue(user.events)
        }
    }

    fun getUniqueCode(): String {
        val zeros = "000000"
        val rnd = Random()
        var s = Integer.toString(rnd.nextInt(0X1000000), 16)
        s = zeros.substring(s.length) + s
        println("s = $s")
        return s
    }

    fun addEventToDatabase(
        event: Event,
        listener: SetOnDuplicateEventCheckListener,
        flag: Boolean = false,
        customEvent: Boolean = false
    ) {
        val eventFetchListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var isDuplicate = false
                var storedKey = ""
                Log.e("Event in add event to db", event.toString());

                // Check if the event already exists in the database
                for (eventObj in dataSnapshot.children) {
                    if (eventObj.child("eventName").value.toString() == event.eventName) {
                        // If an event with the same name already exists, mark it as duplicate
                        storedKey = eventObj.key.toString()
                        isDuplicate = true
                        break
                    }
                }

                if (isDuplicate) {
                    // If it's a duplicate event, notify the listener and handle accordingly
                    listener.onDuplicateEvent()
                } else {
                    // If it's not a duplicate, generate a unique key and add the event to the database
                    val key = getUniqueCode()
                    Log.e("ERROR----> key", key)

                    event.eventCode = key
                    EVENTS_DB.child(key).setValue(event)
                    listener.onEventAdded(key)
                    storedKey = key
                }


                Log.e("ERROR----> storedKey", storedKey)
//                if (!user.events.contains(storedKey)) {
                addEventToUser(storedKey)
//                }


                // Remove the ValueEventListener to prevent memory leaks
                EVENTS_DB.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        }
        EVENTS_DB.addListenerForSingleValueEvent(eventFetchListener)
    }

    fun updateEventOnDatabase(
        event: Event,
        eventID: String,
        oldEventName: String,
        listener: SetOnDuplicateEventCheckListener,
        flag: Boolean = false
    ) {
        val eventFetchListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var flag = 0
                for (eventObj in dataSnapshot.children) {
                    if (eventObj.child("eventName").value.toString() == event.eventName) {
                        flag++
                        break;
                    }
                }
                if (flag != 0 && oldEventName != event.eventName) {
                    listener.onDuplicateEvent()
                } else {
                    EVENTS_DB.child(eventID).setValue(event)
                    listener.onEventAdded(eventID)
                }
                EVENTS_DB.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        EVENTS_DB.addValueEventListener(eventFetchListener)
    }

    interface UserDataListener {
        fun onUserDataLoad(user: User)
    }

    class User(
        var displayName: String,
        var email: String,
        var image: String,
        var events: MutableList<String>
    )

}