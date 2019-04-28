package common.ui.presenter

import common.data.BaseDataRepository
import common.data.api.BaseApiPresenter
import common.ui.contact.SimpleListContract
import javax.inject.Inject

open class SimpleListPresenter<T> @Inject constructor(val dataRepository: BaseDataRepository) : BaseApiPresenter<SimpleListContract.View<T>>(), SimpleListContract.Presenter<T> {
    override fun getListDataRepository()=dataRepository

    override fun loadData(pageIndex: Int, pageSize: Int) {
        view?.showLoadingDialog()
        fetch(view.getDataRepositoryFun(),success = {
            view?.loadDataSuccess(it)
        })
    }

}