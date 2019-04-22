package com.ray.frame.di.component

import com.ray.frame.di.module.ActivityModule
import com.littleturtle.superking.di.module.ApiModule
import com.ray.frame.di.module.ApplicationModule
import com.ray.frame.di.module.CommonModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by gong on 2017/10/18.
 */

@Singleton
@Component(modules = [(ApplicationModule::class), (ApiModule::class)])
interface ApplicationComponent {
    fun plus(activityModule: ActivityModule): ActivityComponent
    fun plus(commonModule: CommonModule): CommonComponent
}