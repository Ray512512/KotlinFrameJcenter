package common

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.multidex.MultiDex
import com.amap.api.maps2d.model.LatLng
import com.kotlin.library.BuildConfig
import com.kotlin.library.R
import com.luseen.logger.LogType
import com.luseen.logger.Logger
import common.presentation.utils.Utils
import common.crash.CrashHandler
import common.data.entry.User
import common.presentation.kotlinx.extensions.get
import common.presentation.utils.Lg
import io.realm.Realm
import io.realm.RealmConfiguration


/**
 * Created by Ray on 2017/10/18.
 */
open class BaseApp : Application() {
   /* val baseAppComponent: BaseAppComponent by unSafeLazy {
        DaggerBaseAppComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }*/

    companion object {
        //全局实例
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: BaseApp
        var token:String = ""
            get() = User.instance.token
            set(value) {
                User.save(value)
                field=value
            }
        //todo 测试坐标
        var lanlng= LatLng(30.551845,104.06555)
    }
    /**
     * 记录当前activity
     */
    var currentActivity: Activity?=null

    override  fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }
    override fun onCreate() {
        super.onCreate()
        if (Utils.getCurrentProcessName(this) != packageName){
            Lg.v("Application",packageName+"不是主进程")
            return
        }
        instance = this
        CrashHandler.getInstance().init(this)
        initLogger()
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build())
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    private fun initLogger() {
        Logger.Builder()
                .isLoggable(BuildConfig.IS_DEBUG)
                .logType(LogType.ERROR)
                .tag(get(R.string.app_tag_name))
                .setIsKotlin(true)
                .build()
    }

    /**
     * 全局activity监听器
     */
    private val activityLifecycleCallbacks=object :ActivityLifecycleCallbacks{
        override fun onActivityPaused(activity: Activity?) {
        }

        override fun onActivityResumed(activity: Activity?) {
        }

        override fun onActivityStarted(activity: Activity?) {
        }

        override fun onActivityDestroyed(activity: Activity?) {
            currentActivity=null
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        }

        override fun onActivityStopped(activity: Activity?) {
        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            currentActivity=activity
        }

    }
}