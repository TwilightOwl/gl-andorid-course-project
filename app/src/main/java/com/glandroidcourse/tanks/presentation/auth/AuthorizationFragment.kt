package com.glandroidcourse.tanks.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.glandroidcourse.tanks.App
import com.glandroidcourse.tanks.R
import com.glandroidcourse.tanks.base.ABaseFragment
import com.glandroidcourse.tanks.base.BaseActivity
import com.glandroidcourse.tanks.presentation.games.GamesActivity
import kotlinx.android.synthetic.main.fragment_authorization.*
import javax.inject.Inject


class AuthorizationFragment: ABaseFragment(), IAuthorizationView {

    @Inject
    @InjectPresenter
    lateinit var presenter: AuthorizationPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    override fun inject() {
        App.appComponent.inject(this)
    }

    override fun getViewId() = R.layout.fragment_authorization

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_authorization, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authButton.setOnClickListener(View.OnClickListener {
            presenter.authorize()
        })

        regButton.setOnClickListener(View.OnClickListener {
            activity?.let {
                if (it is IAuthorizationActivity) {
                    it.showRegistration()
                }
            }
        })

        etLogin.setText(presenter.initialLogin)
        etPassword.setText("")

        etLogin.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.onLoginChange(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        etPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.onPasswordChange(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
    }

    override fun setLoginError(message: String) {
        tvError.visibility = View.VISIBLE
        tvError.text = message
    }

    override fun clearLoginError() {
        tvError.visibility = View.GONE
        tvError.text = ""
    }

    override fun setAuthButtonEnabled(enabled: Boolean) {
        authButton.isEnabled = enabled
    }
}