package com.glandroidcourse.tanks.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.glandroidcourse.tanks.R
import com.glandroidcourse.tanks.ui.auth.AuthorizationFragment

class AuthorizationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        showAuthorization()
    }

    fun showAuthorization() {

//        val fragment: Fragment
//        val backStack: String? = null
//        val tag: String? = null

        supportFragmentManager
            .beginTransaction()
            .add(R.id.container,
                AuthorizationFragment()
            )
            .commit()
    }
}
