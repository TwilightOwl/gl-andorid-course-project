package com.glandroidcourse.tanks.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatFragment

abstract class ABaseFragment : MvpAppCompatFragment() {

    init {
        inject()
    }

    abstract fun inject()
    abstract fun getViewId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getViewId(), container, false)
    }

    fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    protected fun tag() = javaClass.simpleName

}