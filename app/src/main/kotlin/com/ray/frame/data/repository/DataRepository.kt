package com.ray.frame.data.repository

import com.ray.frame.data.api.exception.ApiException
import com.ray.frame.data.api.service.ApiService
import com.ray.frame.data.domain.cache.MemoryCache
import com.ray.frame.data.domain.entity.base.BaseRes
import com.ray.frame.data.domain.entity.simple.Warning
import com.ray.frame.data.domain.fetcher.result_listener.RequestType
import common.data.BaseDataRepository
import common.di.scope.PerActivity
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import javax.inject.Inject

/**
 * Created by Ray on 2017/10/18.
 */
@PerActivity
class DataRepository @Inject constructor(private var apiService: ApiService,
                                         private val memoryCache: MemoryCache): BaseDataRepository() {
    fun <T> handleResult(): FlowableTransformer<BaseRes<T>, T> {
        return FlowableTransformer { upstream ->
            upstream.flatMap { result ->
                if (result.success()) {
                    createData(result.data)
                } else {
                    Flowable.error(ApiException(result.code, result.msg))
                }
            }
        }
    }

    private fun <T> createData(data: T): Flowable<T> {
        return Flowable.create({ emitter ->
            try {
                emitter.onNext(data)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }, BackpressureStrategy.BUFFER)
    }

    fun getWarning(): Flowable<List<Warning>> = apiService.getWarning().compose(handleResult())
    fun getWarningFromMemory(): List<Warning> = memoryCache getCacheForType (RequestType.WARNING)
}