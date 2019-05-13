package common.presentation.kotlinx.extensions

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import java.io.Serializable

/**
 */
inline fun <reified T> Fragment.start() {
    this.startActivity(Intent(context, T::class.java))
}

inline fun <reified T> Fragment.start(startCode:Int) {
    this.startActivityForResult(Intent(activity, T::class.java),startCode)
}

inline fun <reified T> Fragment.start(arg:Map<String,Any>) {
    activity?.let {
        this.startActivity(it.getIntentEx<T>(arg))
    }
}

inline fun <reified T> Fragment.start(startCode:Int,arg:Map<String,Any>) {
    activity?.let {
        this.startActivityForResult(it.getIntentEx<T>(arg),startCode)
    }
}



fun Fragment.withArgument(key: String, value: Any) {
    val args = Bundle()
    when (value) {
        is Int -> args.putInt(key, value)
        is Long -> args.putLong(key, value)
        is String -> args.putString(key, value)
        is Parcelable -> args.putParcelable(key, value)
        is Serializable -> args.putSerializable(key, value)
        else -> throw UnsupportedOperationException("${value.javaClass.simpleName} type not supported yet!!!")
    }
    arguments = args
}

inline infix fun <reified T> Fragment.extraWithKey(key: String): T {
    val value: Any = arguments!![key]
    return value as T
}

fun Fragment.isPortrait() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

infix fun Fragment.takeColor(colorId: Int) = context!!.run { ContextCompat.getColor(this, colorId) }
infix fun Fragment.takeDrawable(resId: Int): Drawable= context!!.run{ContextCompat.getDrawable(this,resId)!!}
inline  fun <reified T> getFragmentInstance(activity: Activity, bundle: Bundle):T{
    val tag = T::class.java.name
    val fragment = Fragment.instantiate(activity, tag)
    fragment.arguments=bundle
    return fragment as T
}
