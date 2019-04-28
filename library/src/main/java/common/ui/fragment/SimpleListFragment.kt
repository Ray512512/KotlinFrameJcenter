package common.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import com.kotlin.library.R
import common.presentation.base_mvp.base.BaseFragment
import common.presentation.kotlinx.extensions.hide
import common.presentation.kotlinx.extensions.show
import common.adapter.RecyleAdapter
import common.data.base.BaseListDataRes
import common.data.base.ListP
import common.ui.contact.PListContract
import common.ui.contact.SimpleListContract
import common.ui.presenter.PListPresenter
import common.ui.presenter.SimpleListPresenter
import common.view.pulltorefreshview.PullToRefreshLayout
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.pull_rec.*
import kotlinx.android.synthetic.main.view_empty.*
import javax.inject.Inject

/**
 * 父类实现加载数据等基础业务逻辑
 * 子类只需关心实际业务
 */
abstract class SimpleListFragment<D>: BaseFragment<SimpleListContract.View<D>, SimpleListContract.Presenter<D>>(), PullToRefreshLayout.OnRefreshListener, SimpleListContract.View<D> {

    override val layoutResId = R.layout.pull_rec_empty

    @Inject
    lateinit var lisePresenter: SimpleListPresenter<D>

    override fun initPresenter()=lisePresenter

    /**
     * 分页参数
     */
    var pageIndex=1
    var pageSize=10
    abstract var adapter: RecyleAdapter<D>

    companion object {
        //懒加载
        const val LAYZ="lazy"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pullto_layout.setOnRefreshListener(this)
        pullable_rec.adapter=adapter
        /**
         * 是否启用懒加载，默认直接加载数据
         */
        arguments?.let {
            if(!it.containsKey(LAYZ)){
                onRefresh()
            }
        }
    }

    override fun lazyFetchData() {
        super.lazyFetchData()
        arguments?.let {
            if(it.containsKey(LAYZ)){
                onRefresh()
            }
        }
    }

    override fun onRefresh() {
        pageIndex=1
        presenter.loadData(pageIndex,pageSize)
    }

    override fun onLoadMore() {
        pageIndex++
        presenter.loadData(pageIndex,pageSize)
    }

    /**
     * 加载数据成功子类回调
     */
   override fun loadDataSuccess(data:ArrayList<D>){
        if(pageIndex==1){
            if(data.isEmpty()){
                empty_root.show()
            }else{
                empty_root.hide()
            }
            pullto_layout.refreshFinish()
            adapter.setAll(data)
        }else{
            if(data.size<pageSize){
                pullto_layout.loadmoreFinish(PullToRefreshLayout.NO_DATA)
            }else{
                pullto_layout.loadmoreFinish(PullToRefreshLayout.SUCCEED)
            }
            adapter.addAll(data)
        }
        if(data.size<pageSize){
            pullable_rec.setCanPullUp(false)
        }
    }
    override fun loadDataFailed(msg:String){
        if(pageIndex==1){
            pullto_layout.refreshFinish()
        }else{
            pullto_layout.loadmoreFinish(PullToRefreshLayout.FAIL)
        }
        showErrorView(msg)
    }

    interface DataCallback{
        fun getDataSuccess(pageIndex:Int,data:Any)
    }

    /**
     * 数据回调
     */
    private  var frameCallback: DataCallback?=null
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            if(context is DataCallback){
                frameCallback=context
            }
        }catch (e:ClassCastException){
            e.printStackTrace()
        }
    }

}
