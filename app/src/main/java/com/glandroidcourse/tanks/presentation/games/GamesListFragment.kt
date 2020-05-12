package com.glandroidcourse.tanks.presentation.games

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.glandroidcourse.tanks.R
import com.glandroidcourse.tanks.game.NetworkPlayer
import com.glandroidcourse.tanks.presentation.game.GameActivity
import kotlinx.android.synthetic.main.fragment_games_list.*

class GamesListFragment: Fragment() {

    lateinit var player: NetworkPlayer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_games_list, container, false)

        val buttonConnect: Button = rootView.findViewById(R.id.buttonConnect) as Button
        buttonConnect.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, GameActivity::class.java)
            startActivity(intent)
        })

        val buttonCreate: Button = rootView.findViewById(R.id.buttonCreate) as Button
        buttonCreate.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, GameActivity::class.java)
            startActivity(intent)
        })

        player = NetworkPlayer()
        player.start()

        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonCreate.setOnClickListener(View.OnClickListener {
            player.ready()
        })
    }
}