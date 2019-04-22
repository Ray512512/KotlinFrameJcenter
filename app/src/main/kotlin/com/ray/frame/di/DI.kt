package com.ray.frame.di

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.ray.frame.di.component.ActivityComponent
import com.ray.frame.di.component.ApplicationComponent
import com.ray.frame.di.module.ActivityModule

/**
 * Created by Ray on 2019/4/18.
 */
fun ApplicationComponent.get(activity:AppCompatActivity):ActivityComponent{
  return  plus(ActivityModule(activity))
}

fun ApplicationComponent.get(f:Fragment):ActivityComponent{
    return  plus(ActivityModule(f.activity as AppCompatActivity))
}