package com.glandroidcourse.tanks.domain.repositories.rest.service

import com.glandroidcourse.tanks.domain.repositories.models.rest.Token
import com.glandroidcourse.tanks.domain.repositories.models.rest.User
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface IUserRestApiService {


    /**
     * Регистрация нового профиля пользователя
     */
    @PUT("/user/v1/registration")
    fun registration(@Body user: User): Observable<User>


    /**
     * Авторизация пользователя по существующему профилю
     */
    @POST("/user/v1/login")
    fun login(@Body user: User): Observable<User>


    /**
     * Будет использовать для обновления текущего токена пользователя
     */
    @POST("/user/v1/refresh")
    @Headers("Content-Type: application/json")
    fun refreshToken(
        @Header("refresh_token") refreshToken: String
    ): Call<Token>
}