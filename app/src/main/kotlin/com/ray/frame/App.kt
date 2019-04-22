package com.ray.frame

import com.ray.frame.di.component.ApplicationComponent
import com.ray.frame.di.component.DaggerApplicationComponent
import com.ray.frame.di.module.ApplicationModule
import common.BaseApp
import common.presentation.kotlinx.extensions.unSafeLazy


/**
 * Created by Ray on 2017/10/18.
 */
class App : BaseApp() {

    companion object {
        val appComponent: ApplicationComponent by unSafeLazy {
            DaggerApplicationComponent.builder()
                    .applicationModule(ApplicationModule(instance))
                    .build()
        }
    }

    override fun onCreate() {
        super.onCreate()
    }
}