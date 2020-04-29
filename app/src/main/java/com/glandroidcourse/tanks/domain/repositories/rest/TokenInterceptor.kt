package com.glandroidcourse.tanks.domain.repositories.rest

import android.util.Log
import com.glandroidcourse.tanks.domain.repositories.models.rest.Token
import com.glandroidcourse.tanks.exceptions.AuthException

import com.glandroidcourse.tanks.domain.repositories.UserRepository
import com.glandroidcourse.tanks.presentation.auth.AuthorizationActivity
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.HttpURLConnection
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject

class TokenInterceptor : Interceptor {

    companion object {

        const val HEADER_AUTHORIZATION = "access_token"
    }


    private val userRepository: UserRepository
    private val lock = ReentrantLock()


    constructor(userRepository: UserRepository) {
        this.userRepository = userRepository
    }


    override fun intercept(inChain: Interceptor.Chain?): Response {

        val chain = inChain ?: throw IllegalArgumentException("Chain is NULL")

        var token = userRepository.getUser()?.token
        if (token == null) {
            AuthorizationActivity.show()
            throw AuthException("Auth is NULL")
        }

        val original = chain.request()
        val response = chain.proceed(addAuth(original, token))

        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            if (lock.tryLock()) {
                try {

                    token = userRepository.refreshToken(token)

                } catch (e: Exception) {
                    e.printStackTrace()
                    AuthorizationActivity.show()
                    throw AuthException("Fail refresh auth")
                } finally {
                    lock.unlock()
                }

                return chain.proceed(addAuth(original, token))
            }

            else {
                lock.lock()
                lock.unlock()

                token = userRepository.getUser()?.token
                if (token == null) {
                    AuthorizationActivity.show()
                    throw AuthException("Auth is NULL")
                }

                return chain.proceed(addAuth(original, token))
            }
        }

        return response
    }


    private fun addAuth(original: Request, token: Token): Request {

        val request = original.newBuilder()

                .removeHeader("Content-Type")
                .removeHeader("Accept")
                .removeHeader(HEADER_AUTHORIZATION)

                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader(HEADER_AUTHORIZATION, token.access)
                .build()

        Log.d("REST", "${request.headers()}")

        return request
    }
}