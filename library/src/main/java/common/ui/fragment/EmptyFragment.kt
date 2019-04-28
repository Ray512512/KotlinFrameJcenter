package common.ui.fragment

import common.presentation.base_mvp.base.BaseFragment
import common.ui.contact.EmptyContract
import common.ui.presenter.EmptyPresenter
import javax.inject.Inject


abstract class EmptyFragment :  BaseFragment<EmptyContract.View, EmptyContract.Presenter>(), EmptyContract.View{

    @Inject
    lateinit var emptyPresenter: EmptyPresenter

    override fun initPresenter()=emptyPresenter

}
