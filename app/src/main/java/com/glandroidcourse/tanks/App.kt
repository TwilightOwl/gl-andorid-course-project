package com.glandroidcourse.tanks

import android.app.Application
import android.content.Context
import com.glandroidcourse.tanks.domain.di.components.AppComponent
import com.glandroidcourse.tanks.domain.di.components.DaggerAppComponent
import io.realm.Realm
import io.realm.RealmConfiguration

class App : Application() {

    companion object {
        lateinit var appContext: Context
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
        appContext = applicationContext
        initRealm()
    }

    private fun initRealm() {

        Realm.init(this)
        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        )
    }
}