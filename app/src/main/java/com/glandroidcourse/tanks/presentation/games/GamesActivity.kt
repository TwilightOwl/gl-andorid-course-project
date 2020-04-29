package com.glandroidcourse.tanks.presentation.games

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.glandroidcourse.tanks.App
import com.glandroidcourse.tanks.R

class GamesActivity : AppCompatActivity() {

    companion object {

        fun show() {
            App.appContext.let {
                it.startActivity(Intent(it, GamesActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        showGamesList()
    }

    fun showGamesList() {

//        val fragment: Fragment
//        val backStack: String? = null
//        val tag: String? = null

        supportFragmentManager
            .beginTransaction()
            .add(R.id.container,
                GamesListFragment()
            )
            .commit()
    }
}
