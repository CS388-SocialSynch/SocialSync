package com.cs388.socialsync

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import java.time.format.DateTimeFormatter
import java.util.Random


object Obj {

    lateinit var USER_DB: DatabaseReference
    lateinit var USERS_DB: DatabaseReference
    lateinit var EVENTS_DB: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var user: User
    var eventList: MutableList<Event> = mutableListOf()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    lateinit var event: Event

    interface SetOnEventFetchListener {
        fun onEventFetch(event : Event)
    }

    fun fetchEventUsingCode(code: String,  listener:SetOnEventFetchListener) {
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

        var count = 0

        user.events.forEach { uid ->
            val aaaa = EVENTS_DB.child(uid)

            val lis = object : ValueEventListener {
                override fun onDataChange(eventSnapshot: DataSnapshot) {
                    val event = createEventFromSnapshot(eventSnapshot)
                    eventList.add(event)
                    updateEventParticipants(event, eventSnapshot)
                    Log.d("Event", event.toString())



                    aaaa.removeEventListener(this)
                    count++
                    if (count == user.events.size) {
                        listener.onDataLoad()
                    }
                    Log.e("CUSTOM---->", "forEach")
                }

                override fun onCancelled(error: DatabaseError) {
                    // i guess we toast an error or something
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


        val event = Event(
            eventSnapshot.child("eventName").value.toString(),
            eventSnapshot.child("startTime").value.toString(),
            eventSnapshot.child("endTime").value.toString(),
            eventSnapshot.child("date").value.toString(),
            eventSnapshot.child("temperature").value as Long?,
            eventSnapshot.child("weatherCondition").value?.toString(),
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
//        val events = mutableListOf<String>()
        user.events.add(key)
        USER_DB.child("events").setValue(user.events)
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
        flag: Boolean = false
    ) {

        val eventFetchListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var flag = 0
                var storedKey = ""

                for (eventObj in dataSnapshot.children) {
                    if (eventObj.child("eventName").value.toString() == event.eventName) {
                        storedKey = eventObj.key.toString()
                        flag++
                        break;
                    }
                }
                if (flag != 0) {
                    listener.onDuplicateEvent()
                    addEventToUser(storedKey)
                } else {
                    val key = getUniqueCode()
                    event.eventCode = key
                    EVENTS_DB.child(key).setValue(event)
                    listener.onEventAdded(key)
                    addEventToUser(key)
                }
                EVENTS_DB.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        EVENTS_DB.addValueEventListener(eventFetchListener)


    }

    fun updateEventOnDatabase(
        event: Event,
        eventID: String,
        listener: SetOnDuplicateEventCheckListener,
        flag: Boolean = false
    ) {
        val eventFetchListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var flag = 0
                var storedKey = ""
                for (eventObj in dataSnapshot.children) {
                    if (eventObj.child("eventName").value.toString() == event.eventName) {
                        storedKey = eventObj.key.toString()
                        flag++
                        break;
                    }
                }
                if (flag != 0) {
                    listener.onDuplicateEvent()
                    addEventToUser(storedKey)
                } else {
                    EVENTS_DB.child(eventID).setValue(event)
                    listener.onEventAdded(eventID)
                    addEventToUser(eventID)
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