package common.ui.contact

import common.data.base.BaseListDataRes
import common.data.BaseDataRepository
import common.data.api.ApiContract
import io.reactivex.Flowable

/**
 * Created by Ray on 2019/1/22.
 * 列表界面共用连接器
 */
interface ListContract{
    interface View<T> : ApiContract.View {
        /**
         * 查询数据成功
         */
        fun loadDataSuccess(data:ArrayList<T>)
        /**
         * 查询列表数据失败
         */
        fun loadDataFailed(msg:String)
        /**
         * 设置调用方法
         */
        fun getDataRepositoryFun(): Flowable<BaseListDataRes<T>>
    }

    interface Presenter<T> : ApiContract.Presenter<View<T>> {
        /**
         * 查询数据
         */
        fun loadData(pageIndex:Int,pageSize:Int)

        /**
         * 获取api仓库
         */
        fun getListDataRepository(): BaseDataRepository
    }
}