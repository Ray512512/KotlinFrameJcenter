package com.ray.frame.presentation.base_mvp.base

import common.presentation.base_mvp.base.BaseContract
import io.armcha.arch.BaseMVPPresenter


/**
 */
abstract class BasePresenter<V : BaseContract.View> : BaseMVPPresenter<V>(), BaseContract.Presenter<V>