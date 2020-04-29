package com.glandroidcourse.tanks.base

import com.glandroidcourse.tanks.base.IRestClient
import java.lang.reflect.ParameterizedType

abstract class ABaseRestApi<S> : IRestApi {

    private val client: IRestClient
    val service: S

    constructor(client: IRestClient) {
        val type = javaClass.genericSuperclass as ParameterizedType
        val clazz = type.actualTypeArguments[0] as Class<S>
        this.client = client
        service = client.createService(clazz)
    }

    override fun cancelAllRequests() {
        client.cancelAllRequests()
    }

}