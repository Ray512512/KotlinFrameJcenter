package common.ui.presenter

import common.ui.contact.ListContract
import common.data.BaseDataRepository
import common.data.api.BaseApiPresenter
import javax.inject.Inject

open class ListPresenter<T> @Inject constructor( val dataRepository: BaseDataRepository) : BaseApiPresenter<ListContract.View<T>>(), ListContract.Presenter<T> {
    override fun getListDataRepository()=dataRepository

    override fun loadData(pageIndex: Int, pageSize: Int) {
        view?.showLoadingDialog()
        fetch(view.getDataRepositoryFun(),success = {
            view?.loadDataSuccess(it.list)
        })
    }

}