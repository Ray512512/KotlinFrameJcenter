package common.ui.presenter

import common.data.BaseDataRepository
import common.data.api.BaseApiPresenter
import common.ui.contact.EmptyContract
import javax.inject.Inject


/**
 * 无需实现任何网络加载数据逻辑P层
 */
 class EmptyPresenter @Inject constructor(private val dataRepository: BaseDataRepository) : BaseApiPresenter<EmptyContract.View>(), EmptyContract.Presenter {}