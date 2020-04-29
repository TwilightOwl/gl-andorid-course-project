package com.glandroidcourse.tanks.domain.repositories.local

import io.realm.DynamicRealm
import io.realm.RealmMigration

class Migration : RealmMigration {

    companion object {

        const val DB_VERSION = 0L
    }

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {

        var version = oldVersion

        if (version++ < newVersion) {

        }

        if (version++ < newVersion) {

        }
    }
}