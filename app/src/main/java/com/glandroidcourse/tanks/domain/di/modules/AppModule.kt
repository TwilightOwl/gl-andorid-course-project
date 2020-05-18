package com.glandroidcourse.tanks.domain.di.modules

import com.glandroidcourse.tanks.base.IRestClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import com.glandroidcourse.tanks.domain.repositories.UserRepository
import com.glandroidcourse.tanks.domain.repositories.local.UserStorage
import com.glandroidcourse.tanks.domain.repositories.rest.RestClient
import com.glandroidcourse.tanks.domain.repositories.rest.TokenInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideUserStorage() = UserStorage()
}