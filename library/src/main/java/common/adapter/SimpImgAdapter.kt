package common.adapter

import android.app.Activity
import com.kotlin.library.R
import com.kotlin.library.glide.GlideUtils
import common.presentation.kotlinx.extensions.onClick
import common.presentation.kotlinx.extensions.show
import common.adapter.base.BaseAdapterHelper
import common.presentation.utils.SizeUtils
import common.presentation.widget.pic.LookBigPicManager

/**
 * Created by Ray on 2018/9/26.
 * 简单图片列表适配器
 */
class SimpImgAdapter constructor(var activity: Activity, var isAdd:Boolean, var data: List<String> = ArrayList(), var cllback: ImgClick?=null, var radius:Int=0) :
        RecyleAdapter<String>(activity, R.layout.simple_img_item, data){

    /**
     * 点击图片
     */
    interface ImgClick{
        fun ImgItemClick(pos:Int)
    }
    companion object {
        const val ADD_KEY="add"
    }

    private fun getShowList():ArrayList<String>{
        val r=ArrayList<String>(data.size)
        for (i in data){
            if(i!=ADD_KEY){
                r.add(i)
            }
        }
        return r
    }

    override fun convert(helper: BaseAdapterHelper, item: String, position: Int) {
        val img = helper.getImageView(R.id.simple_item_img)
        img.onClick {
            if(item.isNotEmpty())
            LookBigPicManager.getInstance().lookBigPic(activity,position,data as ArrayList<String>,img)
            cllback?.ImgItemClick(position)
        }
        if(isAdd){
            val img_delete = helper.getImageView(R.id.img_delete)
            img_delete.show(item.isNotEmpty())
            img_delete.onClick {
                mData.remove(item)
                notifyDataSetChanged()
            }
            if(item.isNotEmpty()){
                GlideUtils.loadRound(activity,item,img,radius)
            }else{
                img.setImageResource(R.mipmap.ic_add_pic)
            }
        }else{
            GlideUtils.loadRound(activity,item,img,radius)
    }
    }
}