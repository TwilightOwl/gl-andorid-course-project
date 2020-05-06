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
class RegistrationPresenter : MvpPresenter<IRegistrationView>{

    private var currentLogin: String = ""
    private var currentPassword: String = ""
    private var currentPasswordConfirm: String = ""

    @Inject
    constructor()

    @Inject
    lateinit var userRepository: UserRepository

    fun register() {
        userRepository.registration(SubRX { _, e ->
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
        validate()
    }

    private fun isLoginValid(): Boolean {
        return currentLogin.length > 2
    }

    private fun isPasswordValid(): Boolean {
        return currentPassword == currentPasswordConfirm
    }

    fun onPasswordChange(value: String) {
        currentPassword = value
        validate()
    }

    fun onPasswordConfirmChange(value: String) {
        currentPasswordConfirm = value
        validate()
    }

    fun validate() {
        val loginValid = isLoginValid()
        val passwordValid = isPasswordValid()

        if (loginValid) {
            viewState.clearLoginError()
        } else {
            viewState.setLoginError("Логин слишком короткий")
        }

        if (passwordValid) {
            viewState.clearPasswordError()
        } else {
            viewState.setPasswordError("Пароли не совпадают")
        }

        viewState.setRegButtonEnabled(loginValid && passwordValid)
    }
}