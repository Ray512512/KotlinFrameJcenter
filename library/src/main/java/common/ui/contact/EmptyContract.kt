package common.ui.contact
import common.data.api.ApiContract


/**
 * Created by Ray on 2018/7/19.
 * 无需实现任何网络加载数据逻辑V层
 */
interface EmptyContract{


    interface View : ApiContract.View


    interface Presenter : ApiContract.Presenter<View>

}