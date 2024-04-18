package com.cs388.socialsync

import java.io.Serializable


data class Event(
    var eventName: String = "",
    var startTime: String? = null,
    var endTime: String? = null,
    var date: String = "",
    var temperature: Int? = null, // in F degrees
    var weatherCondition: String? = null,
    var feelLike: Int? = null,
    var humidity: Int? = null,
    var windSpeed: Int? = null,
    var locationName: String? = null, // *** redundant should be deleted
    var address: String = "", // *** should be location
    var isPublic: Boolean = false, // lets people from outside invite to see the event in the events section
    var showParticipants: Boolean = true, // allows if participants can be seen
    var isInPerson: Boolean = false, // *** used to determine the google maps or extra details
    var hostUID: String = "", // determines the host user
    var optionStartTime: String? = null, // Start time for all days/dates options
    var optionEndTime: String? = null, // End time for all days/dates options
    val optionalDates: MutableList<String> = mutableListOf(), // list of avail days for users to choose from
    val optionalDays: MutableList<String> = mutableListOf(),
    var useSpecificDate: Boolean = false, // used cliently to determine if event has been edited
    var addressStreet: String = "",
    var addressTown: String = "",
    var addressState: String = "",
    var addressCountry: String = "",
    var addressZipCode: String = "",
    var isAPI: Boolean = false,
    val joined: MutableList<String> = mutableListOf(),
    val participants: MutableList<String> = mutableListOf(),
    var eventCode: String = ""

) : Serializable {
    constructor() : this("")

    fun getCombinedAddress(): String{
        return addressStreet + " " + addressTown + " " + addressState + ", " + addressCountry
    }
}