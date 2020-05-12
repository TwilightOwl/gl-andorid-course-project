package com.glandroidcourse.tanks.presentation.auth

import android.app.Activity
import android.content.Intent
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.glandroidcourse.tanks.base.SubRX
import com.glandroidcourse.tanks.domain.repositories.UserRepository
import com.glandroidcourse.tanks.presentation.games.GamesActivity
import javax.inject.Inject

@InjectViewState
class AuthorizationPresenter : MvpPresenter<IAuthorizationView>{

    val initialLogin: String = "superlogin"

    private var currentLogin: String = initialLogin
    private var currentPassword: String = ""

    @Inject
    constructor()

    @Inject
    lateinit var userRepository: UserRepository

    fun authorize() {
        userRepository.login(SubRX { _, e ->
            if (e != null) {
                e.printStackTrace()
                viewState.setLoginError(e.localizedMessage)
                return@SubRX
            }
            GamesActivity.show()
        }, currentLogin, currentPassword)
    }

    fun onLoginChange(login: String) {
        currentLogin = login
        val valid = isLoginValid(currentLogin)
        viewState.setAuthButtonEnabled(valid)
        if (valid || currentLogin.isEmpty()) {
            viewState.clearLoginError()
        } else {
            viewState.setLoginError("Login is too short")
        }
    }

    fun onPasswordChange(password: String) {
        currentPassword = password
    }

    private fun isLoginValid(currentLogin: String): Boolean {
        return currentLogin.length > 2
    }

}