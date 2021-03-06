package com.ray.frame.data.domain.fetcher
import com.google.gson.Gson
import com.ray.frame.App
import com.ray.frame.data.api.exception.ApiException
import com.ray.frame.data.domain.fetcher.result_listener.RequestType
import com.ray.frame.data.domain.fetcher.result_listener.ResultListener
import common.presentation.base_mvp.base.BaseContract
import com.ray.frame.presentation.kotlinx.extensions.log
import com.ray.frame.ui.dispatch.login.LoginAgent
import common.presentation.kotlinx.extensions.showToast
import common.presentation.utils.NetUtils
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import com.ray.frame.data.domain.cache.MemoryCache
import common.BaseApp

/**
 */
//@Singleton
class Fetcher @Inject constructor(private val disposable: CompositeDisposable,
                                  private val memoryCache: MemoryCache) {

    private val requestMap = ConcurrentHashMap<RequestType, Status>()

    var view: BaseContract.View? = null
    private fun <T> getIOToMainTransformer(): SingleTransformer<T, T> {
        return SingleTransformer {
            it.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun <T> fetch(flowable: Flowable<T>, requestType: RequestType,
                  resultListener: ResultListener, success: (T) -> Unit) {
        disposable.add(flowable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { resultListener startAndAdd requestType }
                .subscribe(onSuccess<T>(requestType, success),
                        resultListener.onError(requestType)))
    }

    fun <T> fetch(observable: Observable<T>, requestType: RequestType,
                  resultListener: ResultListener, success: (T) -> Unit) {
        disposable.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { resultListener startAndAdd requestType }
                .subscribe(onSuccess<T>(requestType, success),
                        resultListener.onError(requestType)))
    }

    fun <T> fetch(single: Single<T>, requestType: RequestType,
                  resultListener: ResultListener, success: (T) -> Unit) {
        disposable.add(single
                .compose(getIOToMainTransformer())
                .doOnSubscribe { resultListener startAndAdd requestType }
                .subscribe(onSuccess<T>(requestType, success),
                        resultListener.onError(requestType)))
    }

    fun complete(completable: Completable, requestType: RequestType,
                 resultListener: ResultListener, success: () -> Unit) {
        disposable.add(completable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { resultListener startAndAdd requestType }
                .subscribe({
                    requestMap.replace(requestType, Status.SUCCESS)
                    success()
                }, resultListener.onError(requestType)))
    }

    private infix fun ResultListener.startAndAdd(requestType: RequestType) {
        onRequestStart(requestType)
        if (requestType != RequestType.TYPE_NONE)
            requestMap.put(requestType, Status.LOADING)
    }

    private fun ResultListener.onError(requestType: RequestType): (Throwable) -> Unit {
        return {
            requestMap.replace(requestType, Status.ERROR)
            view?.stopLoadingDialog()
            if (!isCommonError(requestType, it)) {
                onRequestError(requestType, it)
            } else {
                onRequestError(requestType, "")
            }
            log("11---" + Gson().toJson(it))
        }
    }

    fun isCommonError(requestType: RequestType, it: Throwable): Boolean {
        val errorMessage = if (it is ApiException) {
            it.msg
        } else {
            it.message
        }
        var b = false
        fun showErrorView() {
            if (requestType == RequestType.TYPE_NONE) {
                errorMessage?.let {
                    //过滤无需处理的异常
                    if(errorMessage.startsWith("java.lang.Object")){
                        return
                    }
                    view?.showErrorView(it)
                }
            }
        }
        when {
            !NetUtils.checkNetWork(BaseApp.instance) -> { //断网
                view?.showNetWorkErrorView(requestType.dealErrorType)
                b = true
            }
            it is ApiException -> { //服务器公共异常
                when (it.code) {
                    ApiException.ERROR_CODE_TOKEN_INVALID -> {
                        view?.getContext()?.showToast("令牌失效，请重新登录")
                        BaseApp.token = ""
                        LoginAgent.goLogin()
                        b = true
                    }
                    else -> {
                        showErrorView()
                    }
                }
            }
            errorMessage != null && (errorMessage.toLowerCase().contains("timeout") ||
                    errorMessage.toLowerCase().contains("15000ms")) -> { //连接超时
                view?.showErrorView(requestType.dealErrorType)
                b = true
            }
            else -> {
                showErrorView()
            }
        }
        return b
    }

    private fun <T> onSuccess(requestType: RequestType, success: (T) -> Unit): (T) -> Unit {
        return {
            val status = if (it is List<*> && it.isEmpty()) {
                Status.EMPTY_SUCCESS
            } else {
                log("11---"+Gson().toJson(it))
                memoryCache.put(requestType, it)
                Status.SUCCESS
            }
            requestMap.replace(requestType, status)
            view?.showDataView()
            success(it)
        }
    }

    fun hasActiveRequest(): Boolean = requestMap.isNotEmpty()

    fun getRequestStatus(requestType: RequestType) = requestMap.getOrElse(requestType, { Status.IDLE })

    fun removeRequest(requestType: RequestType) {
        requestMap.remove(requestType)
    }

    fun clear() {
        disposable.clear()
    }
}