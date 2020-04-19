package com.glandroidcourse.tanks.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.glandroidcourse.tanks.R
import com.glandroidcourse.tanks.ui.games.GamesActivity


class AuthorizationFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // activity.let {  }

        val rootView: View = inflater.inflate(R.layout.fragment_authorization, container, false)

        val button: Button = rootView.findViewById(R.id.authButton) as Button
        button.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, GamesActivity::class.java)
            startActivity(intent)
        })

        return rootView
    }
}