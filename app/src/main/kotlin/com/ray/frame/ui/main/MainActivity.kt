package com.ray.frame.ui.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.ray.frame.App
import com.ray.frame.R
import com.ray.frame.di.get
import common.presentation.base_mvp.base.BottomTabBaseActivity
import common.view.view.BottomTabView
import javax.inject.Inject


/**
 * Created by Ray on 2017/11/6.
 */
class MainActivity : BottomTabBaseActivity(){
    override fun injectDependencies() {
        App.appComponent.get(this).inject(this)
    }

    private val fragment1 = MainFragment()
    private val fragment2 = MainFragment()
    private val fragment3 = MainFragment()
    private val fragment4 = MainFragment()

    override fun getFragments(): List<Fragment> {
        return arrayListOf(fragment1, fragment2, fragment3, fragment4)
    }
    override val tabViews: List<BottomTabView.TabItemView>
        get() = arrayListOf(
                BottomTabView.TabItemView(this, getString(R.string.app_name), R.color.text_2, R.color.mainColor, R.mipmap.ic_launcher, R.mipmap.ic_launcher),
                BottomTabView.TabItemView(this, getString(R.string.app_name), R.color.text_2, R.color.mainColor, R.mipmap.ic_launcher, R.mipmap.ic_launcher),
                BottomTabView.TabItemView(this, getString(R.string.app_name), R.color.text_2, R.color.mainColor, R.mipmap.ic_launcher, R.mipmap.ic_launcher),
                BottomTabView.TabItemView(this, getString(R.string.app_name), R.color.text_2, R.color.mainColor, R.mipmap.ic_launcher, R.mipmap.ic_launcher)
        )

    companion object {
        var mainActivity: MainActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = this
        initView()
        tryAction()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        tryAction()
    }
    override fun onResume() {
        super.onResume()
        mainActivity = this
    }

    /**
     * 解析跳转意图
     */
    private fun tryAction(){
//        val type = intent.getIntExtra(AppConst.IntentKey.TYPE, 0)
    }

    private fun initView() {

    }
    override fun onDestroy() {
        super.onDestroy()
        mainActivity = null
    }

}