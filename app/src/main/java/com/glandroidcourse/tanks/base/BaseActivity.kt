package com.glandroidcourse.tanks.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.glandroidcourse.tanks.R

abstract class BaseActivity: AppCompatActivity() {

    open fun getContainer(): Int = R.id.container

    fun replace(fragment: Fragment, backStack: String? = null, tag: String? = null) {
        supportFragmentManager
            .beginTransaction()
            .replace(getContainer(), fragment, tag).apply {
                backStack?.let { addToBackStack(it) }
            }
            .commit()
    }

}