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
import kotlinx.android.synthetic.main.fragment_registration.*
import javax.inject.Inject


class RegistrationFragment: ABaseFragment(), IRegistrationView {

    @Inject
    @InjectPresenter
    lateinit var presenter: RegistrationPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    override fun inject() {
        App.appComponent.inject(this)
    }

    override fun getViewId() = R.layout.fragment_registration

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_registration, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        regButton.setOnClickListener(View.OnClickListener {
            presenter.register()
        })

        etLogin.setText("")
        etPassword.setText("")
        etPasswordConfirm.setText("")

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

        etPasswordConfirm.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.onPasswordConfirmChange(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
    }

    override fun setLoginError(message: String) {
        tvLoginError.visibility = View.VISIBLE
        tvLoginError.text = message
    }

    override fun clearLoginError() {
        tvLoginError.visibility = View.GONE
        tvLoginError.text = ""
    }

    override fun setPasswordError(message: String) {
        tvPasswordError.visibility = View.VISIBLE
        tvPasswordError.text = message
    }

    override fun clearPasswordError() {
        tvPasswordError.visibility = View.GONE
        tvPasswordError.text = ""
    }

    override fun setRegButtonEnabled(enabled: Boolean) {
        regButton.isEnabled = enabled
    }
}