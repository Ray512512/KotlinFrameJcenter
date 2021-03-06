package com.ray.frame.ui.dispatch.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.ray.frame.App
import com.ray.frame.presentation.kotlinx.extensions.krealmextensions.deleteAll
import com.ray.frame.presentation.kotlinx.extensions.start
import com.ray.frame.presentation.rxutil.RxBus
import com.ray.frame.presentation.rxutil.RxEvent
import common.BaseApp
import common.data.entry.User
import util.UpdateAppUtils

/**
 * Created by Ray on 2018/12/30.
 * 全局登录代理类
 */
object  LoginAgent {

    /**
     * 启动登录界面
     */
    fun goLogin(activity: Context?=null){
        if(activity==null){
            BaseApp.instance.currentActivity?.start<LoginActivity>()
        }else {
            if(activity is Activity) {
                activity.start<LoginActivity>()
            }else goLoginAndClear()
        }
    }


    /**
     * 启动登录界面并结束之前的界面
     */
    fun goLoginAndClear(activity: Activity?=null){
        val intent = Intent(activity ?: BaseApp.instance as Context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        BaseApp.instance.startActivity(intent)
        BaseApp.instance.sendBroadcast(Intent(UpdateAppUtils.EXITACTION))
    }


    /**
     * 退出登录
     * 清除用户数据
     * 清除购物车数据
     */
    fun logout(activity: Activity) {
        User().deleteAll()
        RxBus.sBus.post(RxEvent.LOGOUT,"")
        User.instance = User()
        User.save()
        goLoginAndClear(activity)
    }
}