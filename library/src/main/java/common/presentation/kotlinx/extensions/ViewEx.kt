package common.presentation.kotlinx.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.Px
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.*
import android.text.method.DigitsKeyListener
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import common.presentation.rxutil.RxFun
import common.presentation.rxutil.RxInterface
import common.data.AppConst
import common.data.entry.User
import common.presentation.simple.SimpleTextWatcher
import common.presentation.utils.AnimaUtil
import common.presentation.utils.SizeUtils
import common.presentation.utils.StatusBarUtils
import common.view.edittext.MyEditText.Companion.PASSWORD_INPUT
import common.view.recycleviewhelper.GridSpacingItemDecoration


/**
 */

fun TextView.leftIcon(drawableId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, drawableId), null, null, null)
}


fun TextView.rightIcon(drawableId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, drawableId), null)
}


fun TextView.rightIcon(resId:Int,padding:Int){
    val drawableLeft = ContextCompat.getDrawable(context,resId)
    setCompoundDrawablesWithIntrinsicBounds(null, null, drawableLeft, null)
    compoundDrawablePadding = SizeUtils.dp2px(context, padding.toFloat())
}


fun TextView.rightIcon(drawable: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
}

fun TextView.rightIcon(resId:Int,padding:Int,iconW:Int,iconH:Int){
    val drawableLeft = ContextCompat.getDrawable(context,resId)
    drawableLeft?.let {
        it.setBounds(0,0,iconW,iconH)
    }
    setCompoundDrawables(null,null,drawableLeft,null)
    compoundDrawablePadding = SizeUtils.dp2px(context, padding.toFloat())
}


fun EditText.string() = this.text.toString()

@SuppressLint("NewApi")
fun TextView.iconTint(colorId: Int) {
    MorAbove {
        compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(context, colorId))
    }
}

var View.scale: Float
    get() = Math.min(scaleX, scaleY)
    set(value) {
        scaleY = value
        scaleX = value
    }

fun View.addTopMargin(@Px marginInPx: Int) {
    (layoutParams as ViewGroup.MarginLayoutParams).topMargin = marginInPx
}

fun View.addBottomMargin(@Px marginInPx: Int) {
    (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = marginInPx
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.showWithAnim() {
    visibility = View.VISIBLE
    AnimaUtil.TranslateShowViewSelfYup(this)
//    startAnim(R.anim.fragment_in)
}

fun View.hideWithAnim() {
    AnimaUtil.TranslateGoneViewSelfYdown(this)
}



fun View.show(v:Boolean){
    visibility = if(v){ View.VISIBLE }else{ View.GONE }
}

fun View.show2(v:Boolean){
    visibility = if(v){ View.VISIBLE }else{ View.INVISIBLE }
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.onClick(time:Long=500,function: () -> Unit) {
    RxFun.clicks(this,time,object : RxInterface.simple{
        override fun action() {
            if(tag is String){
                if(tag.toString() == AppConst.UserKey.NEED_LOGIN_TAG){
                    val activity=context.scanForActivity()
                    if(activity!=null){
                        if(User.checkLogin(activity)){
                            function()
                        }
                    }else{
                        function()
                    }
                }
            }else{
                function()
            }
        }
    })
}

infix fun ViewGroup.inflate(layoutResId: Int): View =
        LayoutInflater.from(context).inflate(layoutResId, this, false)

fun ViewGroup.inflateTo(layoutResId: Int): View =
        LayoutInflater.from(context).inflate(layoutResId, this)

fun ImageView.tint(colorId: Int) {
    setColorFilter(context.takeColor(colorId), PorterDuff.Mode.SRC_IN)
}

operator fun ViewGroup.get(index: Int): View = getChildAt(index)

fun View.asSatusBarView(){
    StatusBarUtils.setStatusBarView(context,this)
}

fun View.startAnim(res:Int){
    AnimationUtils.loadAnimation(context, res)
}

/**
 * 给imageview上色
 */
fun ImageView.setTintColor(colorId:Int,drawableId: Int){
    val d=resources.getDrawable(drawableId)
    DrawableCompat.setTint(d,context.takeColor(colorId))
    setImageDrawable(d)
}

/**
 * 给grid添加间隔距离
 */
fun RecyclerView.gridItemMargin(martin:Int){
    if(layoutManager is GridLayoutManager)
        addItemDecoration(GridSpacingItemDecoration((layoutManager as GridLayoutManager).spanCount, SizeUtils.dp2px(context,martin.toFloat()),false))
}

/**
 *修正RecyclerView高度问题
 */
fun RecyclerView.fixH(activity: Activity){
    val layoutManager = object : LinearLayoutManager(activity) {
        override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
            return RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
    layoutManager.orientation = LinearLayoutManager.VERTICAL
    this.layoutManager = layoutManager
}

fun EditText.inputText(size:Int= Int.MAX_VALUE){
    inputType= InputType.TYPE_CLASS_TEXT
    filters= arrayOf<InputFilter>(InputFilter.LengthFilter(size))
}
fun EditText.inputPhone(){
    inputType= InputType.TYPE_CLASS_PHONE
    filters= arrayOf<InputFilter>(InputFilter.LengthFilter(11))
}

fun EditText.inputPsw(size:Int=16){
    inputType=  InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    filters= arrayOf<InputFilter>(InputFilter.LengthFilter(size))
}

fun EditText.inputPsw2(size:Int=16){
    inputType=  InputType.TYPE_CLASS_TEXT
    filters= arrayOf<InputFilter>(InputFilter.LengthFilter(size))
}

fun EditText.inputEmail(size:Int=20){
    inputType= InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
    keyListener = DigitsKeyListener.getInstance(PASSWORD_INPUT)
    filters= arrayOf<InputFilter>(InputFilter.LengthFilter(size))
}

fun EditText.inputNumber(size:Int=-1){
    inputType= InputType.TYPE_CLASS_NUMBER
    if(size!=-1)
        filters= arrayOf<InputFilter>(InputFilter.LengthFilter(size))
}


fun EditText.inputMaxLength(size:Int){
    filters= arrayOf<InputFilter>(InputFilter.LengthFilter(size))
}

/**
 * 观察输入框输入后事件
 */
fun EditText.watchText(function: () -> Unit){
    addTextChangedListener(object : SimpleTextWatcher(){
        override fun afterTextChanged(s: Editable) {
            super.afterTextChanged(s)
            function.invoke()
        }
    })
}

/**
 * 给文本添加下划线与点击事件
 */
fun TextView.downLineAndClick(function: () -> Unit){
    val spanable = SpannableStringBuilder(text)
    movementMethod = LinkMovementMethod.getInstance()
    spanable.setSpan( object : ClickableSpan(){
        override fun onClick(widget: View?) {
            function()
        }
    },0,text.length , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    text = spanable
    highlightColor = Color.parseColor("#00ffffff")
}
