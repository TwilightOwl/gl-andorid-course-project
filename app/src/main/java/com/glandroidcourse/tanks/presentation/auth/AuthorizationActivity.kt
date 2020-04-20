package com.glandroidcourse.tanks.presentation.auth

import android.os.Bundle
import com.glandroidcourse.tanks.R
import com.glandroidcourse.tanks.base.BaseActivity

class AuthorizationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        showAuthorization()
    }

    fun showAuthorization() {

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
