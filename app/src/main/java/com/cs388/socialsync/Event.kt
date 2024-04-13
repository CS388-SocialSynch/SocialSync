package com.cs388.socialsync

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

data class Event(
    val eventUid: String = "",
    val eventName: String = "",
    val startTime: LocalTime = LocalTime.MIDNIGHT,
    val endTime: LocalTime = LocalTime.MAX,
    val date: LocalDate = LocalDate.now(),
    val temperature: Int? = null, // in F degrees
    val weatherCondition: String? = null,
    val locationName: String? = null,
    val address: String = "", // should be location
    val isHost: Boolean = false,
    val isPublic: Boolean = true,
    val showParticipants: Boolean = true,
    val hostUID: Int = -1,
    val optionalDates: MutableList<LocalDate> = mutableListOf(),
    var participants: MutableList<User> = mutableListOf()
) : Serializable {
    constructor(): this("N/A EVENT")
}