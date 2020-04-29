package com.glandroidcourse.tanks.domain.repositories.local

import com.glandroidcourse.tanks.domain.repositories.models.realm.TokenRealm
import com.glandroidcourse.tanks.domain.repositories.models.realm.UserRealm
import com.glandroidcourse.tanks.domain.repositories.models.rest.Token
import com.glandroidcourse.tanks.domain.repositories.models.rest.User
import com.glandroidcourse.tanks.domain.repositories.models.toBase
import com.glandroidcourse.tanks.domain.repositories.models.toRealm
import io.realm.Realm
import javax.inject.Inject

class UserStorage {

    private var user: User? = null

    @Inject
    constructor()

    fun save(user: User) {
        this.user = user

        Realm.getDefaultInstance().use {
            it.executeTransaction { realm ->
                user.toRealm()?.let { realm.copyToRealmOrUpdate(it) }
            }
        }
    }

    fun save(token: Token) {
        user?.token = token

        Realm.getDefaultInstance().use {
            it.executeTransaction { realm ->
                it.where(UserRealm::class.java).findFirst()?.let {
                    it.token = token.toRealm()
                    realm.copyToRealmOrUpdate(it)
                }
            }
        }
    }

    fun dropCredentials() {
        user = null

        Realm.getDefaultInstance().use {
            it.executeTransaction { realm ->
                it.where(UserRealm::class.java).findAll().deleteAllFromRealm()
                it.where(TokenRealm::class.java).findAll().deleteAllFromRealm()
            }
        }
    }

    fun getUser(): User? {

        user?.let {
            return it
        }

        Realm.getDefaultInstance().use {
            return it.where(UserRealm::class.java).findFirst()?.toBase().apply { user = this }
        }
    }
}