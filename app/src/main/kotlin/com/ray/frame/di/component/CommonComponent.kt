package com.ray.frame.di.component

import com.ray.frame.data.api.NetWorkUtils
import com.ray.frame.di.module.CommonModule
import com.ray.frame.ui.main.MainFragment
import common.di.scope.PerActivity
import common.presentation.utils.NetUtils
import dagger.Subcomponent

/**
 * Created by gong on 2017/10/18.
 */
@PerActivity
@Subcomponent(modules = [(CommonModule::class)])
interface CommonComponent {
    fun inject(f: NetWorkUtils)
}
