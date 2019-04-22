package com.ray.frame.ui.main

import android.os.Bundle
import android.view.View
import com.ray.frame.App
import com.ray.frame.R
import com.ray.frame.di.get
import common.presentation.base_mvp.base.BaseFragment
import javax.inject.Inject

/**
 * Created by Ray on 2018/11/27.
 */
class MainFragment : BaseFragment<MainContract.View, MainContract.Presenter>(),MainContract.View {

    override fun injectDependencies() {
        App.appComponent.get(this).inject(this)
    }
    @Inject
    lateinit var mainPresenter: MainPresenter
    override fun initPresenter()=mainPresenter

    override val layoutResId = R.layout.content_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun lazyFetchData() {
    }

}