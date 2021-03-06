package common.presentation.kotlinx.extensions

import common.data.AppConst
import common.presentation.utils.FileUtils
import common.presentation.utils.Lg
import common.presentation.utils.StringUtils
import okhttp3.RequestBody
import java.io.File
import java.text.DecimalFormat
import java.util.*

/**
 * Created by Ray on 2019/2/1.
 */

fun File.getFileRequestMap(): HashMap<String, RequestBody> {
    val map = HashMap<String, RequestBody>()
    try {
        val file = FileUtils.getCompressFile(this.path)
        Lg.v("getFileRequestMap", "${file.length()/1024}")
        val fileBody = RequestBody.create(AppConst.MediaType.IMAGE, file)
        map["files\"; filename=\"" + file.name] = fileBody
    } catch (e: Exception) {
        throw Exception()
    }
    return map
}


fun HashMap<kotlin.String, kotlin.IntArray>.putWithSelf(k:kotlin.String, v:kotlin.IntArray):HashMap<kotlin.String, kotlin.IntArray>{
    this[k] = v
    return this
}


fun <K,V>HashMap<K,V>.putWithSelf(k:K, v:V):HashMap<K, V>{
    this[k] = v
    return this
}

fun <E> ArrayList<E>.put(e:E): ArrayList<E> {
    add(e)
    return this
}

fun Int.getRandom():Int{
    return Random().nextInt(this)
}



fun List<String>.getStrings(tag:String=","):String{
    if(isEmpty())return ""
    var r = ""
    for (i in this) {
        if(i.isEmpty())continue
        r += i+tag
    }
    return StringUtils.cutEndTag(r, tag)
}

fun String.cutEndTag(tag:String):String{
    return StringUtils.cutEndTag(this,tag)
}

/**
 * 逗号字符串转集合
 */
fun String.getList(maxSize:Int= Int.MAX_VALUE):List<String>{
    val list=ArrayList<String>()
    if(this.isEmpty())return list
    for (i in this.split(",")){
        if(list.size<maxSize){
            list.add(i)
        }else{
            break
        }
    }
    return list
}

/**
 * 获取逗号分隔的字符串
 */
fun ArrayList<String>.getStr():String{
    var s=""
    for (i in this){
        s+= "$i,"
    }
    s=s.cutEndTag(",")
    return s
}

fun ArrayList<String>.getList(maxSize: Int):ArrayList<String>{
    val r=ArrayList<String>()
    for (i in this.indices){
        if(i>=maxSize){
            break
        }
        r.add(this[i])
    }
    return r
}
/**
 * 保留两位小数
 */
fun Double.getSimpleStr():String{
    return DecimalFormat("##0.00").format(this)
}

fun Double.getMoneyStr():String{
    return "￥"+ DecimalFormat("##0.00").format(this)
}

/**
 * 保留两位小数
 */
fun Double?.getSimpleStr():String{
    if(this==null)return "0.00"
    return DecimalFormat("##0.00").format(this)
}


fun Double?.getMoneyStr():String{
    if(this==null)return "￥"+"0.00"
    return "￥"+ DecimalFormat("##0.00").format(this)
}

fun String?.getNotNullStr():String{
    if(isNullOrEmpty())return ""
    return this!!
}


fun Int?.getNotNullInt():Int{
    if(this==null)return 0
    return this
}


fun Float?.getNotNullFolat():Float{
    if(this==null)return 0F
    return this
}
/**
 * 保留两位小数
 */
fun Double?.getIntStr():String{
    if(this==null)return "0"
    return DecimalFormat("##0").format(this)
}

fun <E>ArrayList<E>.putAll( c: Collection<E>):ArrayList<E>{
    addAll(c)
    return this
}

fun String?.getRealOrNull():String?{
    return if(isNullOrEmpty()) null else this
}

/**
 * 保留两位小数
 */
fun Double?.custMoney():String{
    if(this==null)return "0.00"
    return StringUtils.castMoney(this)
}


inline fun <reified T>  ArrayList<T>?.isNullOrEmpty():Boolean {
    return this == null || isEmpty()
}

