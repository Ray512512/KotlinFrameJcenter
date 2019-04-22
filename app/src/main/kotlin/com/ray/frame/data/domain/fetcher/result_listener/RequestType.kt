package com.ray.frame.data.domain.fetcher.result_listener

import common.data.base.DealErrorType
/**
 */
enum class RequestType constructor(var dealErrorType: DealErrorType = DealErrorType.DIALOG){
    WARNING,
    TYPE_NONE,
}