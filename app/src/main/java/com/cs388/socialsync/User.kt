package com.cs388.socialsync

import java.io.Serializable

data class User(
    val name: String,
    var attending: Boolean,
) : Serializable