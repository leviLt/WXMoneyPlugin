package com.wechatplugin.scropio.wxmoneyplugin.http.net_coroutines

import com.wechatplugin.scropio.wxmoneyplugin.http.net_coroutines.response.ResultResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApi {

    /**
     * 获取动作
     */
    @GET
    suspend fun getAction(
        @Query("params") params: String
    ): ResultResponse<Any>

}