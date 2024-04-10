package com.cs388.socialsync

import android.R
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

data class Event(
    val eventName: String = "",
    val startTime: LocalTime = LocalTime.MIDNIGHT,
    val endTime: LocalTime = LocalTime.MAX,
    val date: LocalDate = LocalDate.now(),
    val temperature: Int? = null, // in F
    val weatherCondition: String? = null,
    val locationName : String? = null, // redundant should be deleted
    val address : String = "", // should be location
    val showParticipants: Boolean = true,
    val isHost: Boolean = false,
    val isPublic: Boolean = true,
    val hostUID: Int = -1,
    val optionalDates: MutableList<LocalDate> = mutableListOf()
) : Serializable {
    constructor(): this("N/A EVENT")
}