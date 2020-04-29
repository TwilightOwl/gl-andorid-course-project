package com.glandroidcourse.tanks.presentation.auth

import android.content.Intent
import android.os.Bundle
import com.glandroidcourse.tanks.App
import com.glandroidcourse.tanks.R
import com.glandroidcourse.tanks.base.BaseActivity

class AuthorizationActivity : BaseActivity(), IAuthorizationActivity {

    companion object {
        fun show() {
            App.appContext.let {
                it.startActivity(Intent(it, AuthorizationActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        if (savedInstanceState != null) return

        showAuthorization()
        // showRegistration()
    }

    override fun showRegistration() {
        replace(
            RegistrationFragment()
        )
    }

    override fun showAuthorization() {

//        val fragment: Fragment
//        val backStack: String? = null
//        val tag: String? = null

        replace(
            AuthorizationFragment()
        )

//        supportFragmentManager
//            .beginTransaction()
//            .add(R.id.container,
//                AuthorizationFragment()
//            )
//            .commit()
    }
}
