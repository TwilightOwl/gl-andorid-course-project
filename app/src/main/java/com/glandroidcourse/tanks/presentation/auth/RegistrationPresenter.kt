package com.glandroidcourse.tanks.presentation.auth

import android.app.Activity
import android.content.Intent
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.glandroidcourse.tanks.presentation.games.GamesActivity
import javax.inject.Inject

@InjectViewState
class RegistrationPresenter : MvpPresenter<IRegistrationView>{

    val initialLogin: String = ""

    private var currentLogin: String = initialLogin

    fun authorize() {
        // playerRepository.authorize(login)
        // viewState.setLoginError("Error message")
//        val intent = Intent(activity, GamesActivity::class.java)
//        startActivity(intent)

    }

    @Inject
    constructor()

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

    private fun isLoginValid(currentLogin: String): Boolean {
        return currentLogin.length > 2
    }

}