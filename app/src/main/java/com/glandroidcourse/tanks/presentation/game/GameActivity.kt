package com.glandroidcourse.tanks.presentation.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.glandroidcourse.tanks.R
import com.glandroidcourse.tanks.base.BaseActivity
import com.glandroidcourse.tanks.game.engine.GameProcess


class GameActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_container)
        //setContentView(GameView(this))

        //showGamesList()

        if (savedInstanceState != null) return
        replace(
            GameFragment()
        )
    }

//    fun showGamesList() {
//
////        val fragment: Fragment
////        val backStack: String? = null
////        val tag: String? = null
//
//        supportFragmentManager
//            .beginTransaction()
//            .add(R.id.container,
//                GameFragment()
//            )
//            .commit()
//    }
}
