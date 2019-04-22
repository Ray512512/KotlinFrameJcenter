package com.ray.frame.di.component

import com.ray.frame.di.module.CommonModule
import common.di.scope.PerActivity
import dagger.Subcomponent

/**
 * Created by gong on 2017/10/18.
 */
@PerActivity
@Subcomponent(modules = [(CommonModule::class)])
interface CommonComponent {

}
