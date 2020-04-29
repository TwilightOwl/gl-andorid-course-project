package com.glandroidcourse.tanks.domain.repositories.models.rest

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int? = null,
    val login: String,
    val password: String,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    var token: Token? = null
)