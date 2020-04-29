package com.glandroidcourse.tanks.domain.repositories.rest.api

import com.glandroidcourse.tanks.base.IRestClient
import com.glandroidcourse.tanks.base.ABaseRestApi
import com.glandroidcourse.tanks.domain.repositories.models.rest.User
import com.glandroidcourse.tanks.domain.di.modules.NetModule
import com.glandroidcourse.tanks.domain.repositories.rest.service.IUserRestApiService
import javax.inject.Inject
import javax.inject.Named

class UserRestApi : ABaseRestApi<IUserRestApiService> {


    @Inject
    constructor(@Named(NetModule.NAME_AUTH_REST_CLIENT) client: IRestClient) : super(client)


    fun registration(login: String, password: String)
            = service.registration(
        User(
            login = login,
            password = password
        )
    )


    fun login(login: String, password: String)
            = service.login(
        User(
            login = login,
            password = password
        )
    )


    fun refreshToken(refreshToken: String)
            = service.refreshToken(refreshToken)
}