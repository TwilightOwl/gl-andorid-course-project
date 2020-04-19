package com.glandroidcourse.tanks.ui.games

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.glandroidcourse.tanks.R

class GamesActivity : AppCompatActivity() {

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
