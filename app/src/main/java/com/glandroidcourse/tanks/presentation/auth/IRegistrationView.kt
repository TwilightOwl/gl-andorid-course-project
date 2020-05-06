package com.glandroidcourse.tanks.presentation.auth

import com.arellomobile.mvp.MvpView

interface IRegistrationView : MvpView {
    fun setLoginError(message: String)
    fun setPasswordError(message: String)
    fun clearLoginError()
    fun clearPasswordError()
    fun setRegButtonEnabled(enabled: Boolean)
}