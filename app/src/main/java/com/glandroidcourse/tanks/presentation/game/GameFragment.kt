package com.glandroidcourse.tanks.presentation.game

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.glandroidcourse.tanks.App
import com.glandroidcourse.tanks.R
import com.glandroidcourse.tanks.base.ABaseFragment
import com.glandroidcourse.tanks.game.engine.*
import com.glandroidcourse.tanks.game.engine.map.Direction
import com.glandroidcourse.tanks.game.engine.map.Position
import kotlinx.android.synthetic.main.fragment_game.*

import javax.inject.Inject


class GameFragment: ABaseFragment(), IGameFragment {

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

    var gameView: GameView? = null

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
        gameView = GameView(requireContext(), presenter)
        gameFieldFrameLayout.addView(gameView)

        // btnStart.setOnClickListener({ presenter.start() })
        btnFire.setOnClickListener(View.OnClickListener { presenter.fire() })

        btnDown.setOnClickListener(View.OnClickListener { presenter.goDown() })
        btnDown.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_MOVE) {
                    presenter.goDown()
                }
                return v?.onTouchEvent(event) ?: true
            }
        })

        btnUp.setOnClickListener(View.OnClickListener { presenter.goUp() })
        btnUp.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_MOVE) {
                    presenter.goUp()
                }
                return v?.onTouchEvent(event) ?: true
            }
        })

        btnRight.setOnClickListener(View.OnClickListener { presenter.goRight() })
        btnRight.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_MOVE) {
                    presenter.goRight()
                }
                return v?.onTouchEvent(event) ?: true
            }
        })

        btnLeft.setOnClickListener(View.OnClickListener {})
        btnLeft.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_MOVE) {
                    presenter.goLeft()
                }
                return v?.onTouchEvent(event) ?: true
            }
        })
    }


    override fun onStateChanged(state: Map<GameObjectName, List<Pair<IGameObject, Position>>>) {
        if (gameView != null) {
            gameView!!.onStateChanged(state)
        }
    }
}