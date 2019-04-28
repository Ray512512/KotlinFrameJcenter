package common.data.api

import common.presentation.base_mvp.base.BasePresenter
import common.presentation.base_mvp.base.BaseContract
import io.reactivex.Flowable

/**
 */

abstract class BaseApiPresenter<VIEW : BaseContract.View> : BasePresenter<VIEW>(){

     open  fun <TYPE> fetch(flowable: Flowable<TYPE>, success: (TYPE) -> Unit) {}
}