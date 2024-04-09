package com.cs388.socialsync


data class PublicEvent(
    val eventName: String,
    val startTime: String,
    val endTime: String,
    val date: String,
    val temperature: Int,
    val weatherCondition: String
)
