package com.ray.frame.ui.main

import common.data.api.ApiContract

/**
 * Created by Ray on 2017/10/18.
 */
interface MainContract {
    interface View : ApiContract.View {
    }

    interface Presenter : ApiContract.Presenter<View> {

    }
}