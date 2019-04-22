package com.ray.frame.ui.dispatch

import common.presentation.base_mvp.base.BaseContract


/**
 */
interface DispatchContract {

    interface View : BaseContract.View {

        fun initPermission()

        fun requestPermission()

        fun openHomeActivity()

        fun openLoginActivity()
    }

    interface Presenter : BaseContract.Presenter<View>
}