package com.cs388.socialsync

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

data class Event(
    var eventName: String = "",
    var startTime: LocalTime? = null,
    var endTime: LocalTime? = null,
    var date: LocalDate = LocalDate.now(),
    var temperature: Int? = null, // in F degrees
    var weatherCondition: String? = null,
    var locationName : String? = null, // *** redundant should be deleted
    var address : String = "", // *** TODO should be deleted
    var isHost: Boolean = false, // *** TODO should be deleted
    var isPublic: Boolean = false, // lets people from outside invite to see the event in the events section
    var showParticipants: Boolean = true, // allows if participants can be seen
    var isInPerson: Boolean = false, // *** used to determine the google maps or extra details
    var hostUID: String = "", // determines the host user
    var optionStartTime: LocalTime? = null, // Start time for all days/dates options
    var optionEndTime: LocalTime? = null, // End time for all days/dates options
    val optionalDates: MutableList<LocalDate> = mutableListOf(), // list of avail days for users to choose from
    val optionalDays: MutableList<String> = mutableListOf(),
    var useSpecificDate: Boolean = false, // used cliently to determine if event has been edited
    var addressStreet:String ="",
    var addressTown:String ="",
    var addressState:String ="",
    var addressCountry:String ="",
    var isAPI:Boolean=false,
    val participants: MutableList<String> = mutableListOf(),
    var eventCode: Int = -1
) : Serializable {
    constructor(): this("")
}