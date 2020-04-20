package com.glandroidcourse.tanks.presentation.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.glandroidcourse.tanks.R

class GameActivity : AppCompatActivity() {

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
                GameFragment()
            )
            .commit()
    }
}
