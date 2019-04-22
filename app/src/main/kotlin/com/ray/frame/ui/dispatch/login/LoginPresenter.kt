package com.ray.frame.ui.dispatch.login

import common.di.scope.PerActivity
import com.ray.frame.data.api.ApiPresenter
import javax.inject.Inject
import com.ray.frame.data.repository.DataRepository

/**
 */
@PerActivity
class LoginPresenter @Inject constructor(private val dataRepository: DataRepository)
    : ApiPresenter<LoginContract.View>(), LoginContract.Presenter {

    override fun requestLoginByPsw(account: String, psw: String,isShow:Boolean) {

    }

}