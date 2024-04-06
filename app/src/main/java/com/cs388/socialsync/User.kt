package com.cs388.socialsync

import java.io.Serializable

data class User(
    val firstName: String,
    val attending: String,
) : Serializable