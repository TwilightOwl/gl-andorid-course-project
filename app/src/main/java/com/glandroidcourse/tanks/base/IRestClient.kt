package com.glandroidcourse.tanks.base

interface IRestClient {
    fun <S> createService(serviceClass: Class<S>): S
    fun cancelAllRequests()
}