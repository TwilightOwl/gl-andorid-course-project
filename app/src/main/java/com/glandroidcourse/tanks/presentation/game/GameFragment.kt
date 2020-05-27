package com.glandroidcourse.tanks.presentation.game

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.glandroidcourse.tanks.App
import com.glandroidcourse.tanks.R
import com.glandroidcourse.tanks.base.ABaseFragment
import kotlinx.android.synthetic.main.fragment_game.*

import javax.inject.Inject


class GameFragment: ABaseFragment(), IGameView {

    @Inject
    @InjectPresenter
    lateinit var presenter: GamePresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    override fun inject() {
        App.appComponent.inject(this)
    }

    override fun getViewId(): Int {
        return R.layout.fragment_game
    }

//    private var mContext: Context? = null
//
//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//        mContext = context
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_game, container, false)
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameFieldFrameLayout.addView(GameView(requireContext(), presenter))
        circleButtonLeft.setOnClickListener(View.OnClickListener {
            presenter.left()
        })
        circleButtonRight.setOnClickListener(View.OnClickListener {
            presenter.right()
        })
    }
}