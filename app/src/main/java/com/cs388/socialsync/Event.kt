package com.cs388.socialsync

import android.R
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

data class Event(
    var eventName: String = "",
    var startTime: LocalTime = LocalTime.MIDNIGHT,
    var endTime: LocalTime = LocalTime.MAX,
    var date: LocalDate = LocalDate.now(),
    var temperature: Int? = null, // in F degrees
    var weatherCondition: String? = null,
    var locationName : String? = null, // *** redundant should be deleted
    var address : String = "", // *** should be location
    var isHost: Boolean = false, // *** should be deleted
    var isPublic: Boolean = true, // lets people from outside invite to see the event in the events section
    var showParticipants: Boolean = true, // allows if participants can be seen
    var isInPerson: Boolean = true, // *** used to determine the google maps or extra details
    var hostUID: Int = -1, // determines the host user
    var optionalDates: MutableList<LocalDate> = mutableListOf(), // list of avail days for users to choose from
    var edited: Boolean = false // used cliently to determine if event has been edited
) : Serializable {
    constructor(): this("ERROR EVENT")
}

/*
Defaults for public
    isHost=false
    isPublic=true
    showParticipants=false
    isInPerson=[true/false]
 */

/*
Defaults for normal event
    isPublic=false
    showParticipants=true
    isInPerson=[true/false]
 */