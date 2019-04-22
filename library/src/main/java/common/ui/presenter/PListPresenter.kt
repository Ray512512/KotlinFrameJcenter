package common.ui.presenter

import common.ui.contact.PListContract
import common.data.BaseDataRepository
import common.data.api.BaseApiPresenter
import javax.inject.Inject

open class PListPresenter<T> @Inject constructor(val dataRepository: BaseDataRepository) : BaseApiPresenter<PListContract.View<T>>(), PListContract.Presenter<T> {


    override fun getListDataRepository()=dataRepository

    override fun loadData(pageIndex: Int, pageSize: Int, key: String) {
        view?.showLoadingDialog()
        view.getDataRepositoryFun2()?.let {
            fetch(it,success = {
                view?.loadData(it)
                view?.loadDataSuccess(it.getListD(key).list)
            })
        }
        view?.getDataRepositoryFun()?.let {
            fetch(it,success = {
                view?.loadDataSuccess(it.list)
            })
        }
    }

}