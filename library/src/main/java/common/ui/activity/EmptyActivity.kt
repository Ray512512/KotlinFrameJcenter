package common.ui.activity

import android.os.Bundle
import common.ui.presenter.EmptyPresenter
import common.ui.contact.EmptyContract
import common.presentation.base_mvp.base.BaseActivity
import javax.inject.Inject

/**
 * 无需实现任何网络加载数据逻辑界面
 */
abstract class EmptyActivity : BaseActivity<EmptyContract.View, EmptyContract.Presenter>(), EmptyContract.View {
   
    @Inject
    lateinit var emptyPresenter: EmptyPresenter

    override fun initPresenter()=emptyPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
