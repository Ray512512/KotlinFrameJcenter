package com.ray.frame.data.api

import com.kotlin.library.R
import com.ray.frame.App
import com.ray.frame.data.api.exception.ApiException
import com.ray.frame.data.repository.DataRepository
import com.ray.frame.di.get
import common.presentation.base_mvp.base.BaseActivity
import common.presentation.kotlinx.extensions.showToast
import common.presentation.simple.SimpleSubscriber
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Ray on 2019/3/13.
 */
class NetWorkUtils {

    @Inject
    lateinit var api: DataRepository
    init {
        App.appComponent.get().inject(this)
    }

      fun <T> action(activity: BaseActivity<*, *>, f: Flowable<T>, func:(T)->Unit) {
        activity.showLoadingDialog()
        f.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(object : SimpleSubscriber<T>() {
            override fun onNext(t: T) {
                activity.stopLoadingDialog()
                func(t)
            }

            override fun onError(e: Throwable?) {
                super.onError(e)
                activity.stopLoadingDialog()
                if (!ApiException.dealThrowable(activity, e)) {
                    activity.showToast(R.string.ask_failed)
                }
            }
        })
    }
}