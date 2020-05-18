package com.glandroidcourse.tanks.domain.repositories.models.rest

data class Token(
    val access: String,
    var refresh: String
)