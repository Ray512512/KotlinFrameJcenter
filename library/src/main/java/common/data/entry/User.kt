package common.data.entry

import android.content.Context
import common.presentation.kotlinx.extensions.krealmextensions.deleteAll
import common.presentation.kotlinx.extensions.krealmextensions.queryFirst
import common.presentation.kotlinx.extensions.krealmextensions.save
import io.realm.RealmObject

/**
 * Created by Ray on 2018/12/30.
 * 用户个人信息类
 */
open class User constructor(
        var token: String=""
): RealmObject(){
    companion object {
        var instance = User()
            get() {
                if(field.token.isEmpty()){
                    try {
                        User().queryFirst()?.let {
                            field=it
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                return field
            }

        /**
         * 保存 先删除本地重新保存
         */
        fun save(){
            delete()
            instance.save()
        }

        /**
         * 保存token
         */
        fun save(token:String){
            delete()
            instance.token=token
            instance.save()
        }

        /**
         * 删除本地
         */
        fun delete(){
            instance.deleteAll()
        }

        /**
         * 是否能够登录
         */
        fun needLogin():Boolean{
            return  instance.token.isEmpty()
        }

        /**
         * 检查登录状态
         */
        fun checkLogin(activity: Context):Boolean{
            if(User.needLogin()){
                return false
            }
            return true
        }


    }

}