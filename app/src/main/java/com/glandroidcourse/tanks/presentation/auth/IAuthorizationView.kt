package com.glandroidcourse.tanks.presentation.auth

import com.arellomobile.mvp.MvpView

interface IAuthorizationView : MvpView {
    fun setLoginError(message: String)
    fun clearLoginError()
    fun setAuthButtonEnabled(enabled: Boolean)
}