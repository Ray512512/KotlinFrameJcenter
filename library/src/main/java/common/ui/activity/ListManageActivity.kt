package common.ui.activity

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.freelib.multiitem.adapter.BaseItemAdapter
import com.freelib.multiitem.animation.AlphaInAnimation
import com.kotlin.library.R
import common.presentation.base_mvp.base.BaseActivity
import common.presentation.kotlinx.extensions.show
import common.presentation.kotlinx.extensions.takeDrawable
import common.ui.contact.SimpleListContract
import common.ui.presenter.SimpleListPresenter
import common.view.pulltorefreshview.PullToRefreshLayout
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.pull_rec.*
import javax.inject.Inject

/**
 * 父类实现加载数据等基础业务逻辑
 * 子类只需关心实际业务
 */
abstract class ListManageActivity<D>: BaseActivity<SimpleListContract.View<D>, SimpleListContract.Presenter<D>>(), PullToRefreshLayout.OnRefreshListener, SimpleListContract.View<D> {
    protected open var redId= R.layout.activity_list

    @Inject
    lateinit var lisePresenter: SimpleListPresenter<D>
    /**
     * 设置标题
     */
    abstract var titleStr:String
    override fun initPresenter()=lisePresenter
    /**
     * 分页参数
     */
    var pageIndex=1
    var pageSize=10
     var adapter= BaseItemAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(redId)
        super.onCreate(savedInstanceState)
        list_title?.setTitleText(titleStr)
        list_title.show()
        list_title.setBackListener {
            finish()
        }
        pullto_layout.background=takeDrawable(R.color.transparent)
        pullto_layout.setOnRefreshListener(this)
        pullable_rec.adapter=adapter
        adapter.enableAnimation(AlphaInAnimation(), true)
        onRefresh()
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
            stopLoadingDialog()
            pullto_layout.refreshFinish()
            adapter.clearFoot()
            if(data.isEmpty()) {
                val emptyV=View.inflate(this,R.layout.empty_view_bg_gray,null)
                emptyV.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
                adapter.addFootView(emptyV)
            }
            adapter.setDataItems(data)
        }else{
            if(data.size<pageSize){
                pullto_layout.loadmoreFinish(PullToRefreshLayout.NO_DATA)
            }else{
                pullto_layout.loadmoreFinish(PullToRefreshLayout.SUCCEED)
            }
            adapter.addDataItems(data)
        }
        pullable_rec.setCanPullUp(data.size>=pageSize)
    }


    override fun loadDataFailed(msg:String){
        if(pageIndex==1){
            pullto_layout.refreshFinish()
        }else{
            pullto_layout.loadmoreFinish(PullToRefreshLayout.FAIL)
        }
        showErrorView(msg)
    }
}
