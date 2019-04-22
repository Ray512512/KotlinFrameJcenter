package com.ray.frame.di.component

import com.ray.frame.di.module.ActivityModule
import com.ray.frame.ui.dispatch.DispatchActivity
import com.ray.frame.ui.dispatch.login.LoginActivity
import com.ray.frame.ui.main.MainActivity
import com.ray.frame.ui.main.MainFragment
import common.di.scope.PerActivity
import dagger.Subcomponent

/**
 * Created by gong on 2017/10/18.
 */
@PerActivity
@Subcomponent(modules = [(ActivityModule::class)])
interface ActivityComponent {

fun inject(f:MainFragment)
fun inject(f:DispatchActivity)
fun inject(f:LoginActivity)
fun inject(f: MainActivity)
}
