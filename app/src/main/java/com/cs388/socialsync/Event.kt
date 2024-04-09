package com.cs388.socialsync

import java.io.Serializable

data class Event(
    val eventName: String,
    val startTime: String,
    val endTime: String,
    val date: String,
    val temperature: Int,
    val weatherCondition: String,
    val locationName : String,
    val address : String,
    val isHost: Boolean,
    val isPublic: Boolean
) : Serializable