package com.glandroidcourse.tanks.domain.repositories

import android.os.SystemClock
import com.glandroidcourse.tanks.base.SubRX
import com.glandroidcourse.tanks.base.standardSubscribeIO
import com.glandroidcourse.tanks.domain.repositories.models.rest.Token
import com.glandroidcourse.tanks.domain.repositories.models.rest.User
import com.glandroidcourse.tanks.domain.repositories.local.UserStorage
import com.glandroidcourse.tanks.domain.repositories.rest.api.UserRestApi
import java.net.HttpURLConnection
import javax.inject.Inject

class UserRepository {

    private val storage: UserStorage
    private val rest: UserRestApi

    @Inject
    constructor(storage: UserStorage, rest: UserRestApi) {
        this.storage = storage
        this.rest = rest
    }

    fun registration(observer: SubRX<User>, login: String, pass: String) {

        rest.registration(login, pass)
            .doOnNext { storage.save(it) }
            .standardSubscribeIO(observer)
    }

    fun login(observer: SubRX<User>, login: String, pass: String) {

        rest.login(login, pass)
            .doOnNext { storage.save(it) }
            .standardSubscribeIO(observer)
    }

    fun getUser() = storage.getUser()

    fun refreshToken(token: Token, onRetry: (Int) -> Boolean = { it == HttpURLConnection.HTTP_UNAUTHORIZED }): Token? {

        val response = rest.refreshToken(token.refresh).execute()
        response.body()?.let {
            storage.save(it)
            return it
        }

        if (onRetry(response.code())) {
            SystemClock.sleep(500)
            return refreshToken(token)
        }

        return null
    }
}