package com.glandroidcourse.tanks.domain.repositories.models

import com.glandroidcourse.tanks.domain.repositories.models.realm.TokenRealm
import com.glandroidcourse.tanks.domain.repositories.models.realm.UserRealm
import com.glandroidcourse.tanks.domain.repositories.models.rest.Token
import com.glandroidcourse.tanks.domain.repositories.models.rest.User


fun Token?.toRealm(): TokenRealm? {
    this ?: return null

    return TokenRealm().let {
        it.access = access
        it.refresh = refresh
        it
    }
}

fun TokenRealm?.toBase(): Token? {
    this ?: return null
    return Token(access ?: "", refresh ?: "")
}


fun User?.toRealm(): UserRealm? {
    this ?: return null

    return UserRealm().let {
        it.id = id ?: 0
        it.login = login
        it.password = password
        it.avatarUrl = avatarUrl
        it.token = token.toRealm()
        it
    }
}

fun UserRealm?.toBase(): User? {
    this ?: return null
    return User(id, login ?: "", password ?: "", avatarUrl, token.toBase())
}
