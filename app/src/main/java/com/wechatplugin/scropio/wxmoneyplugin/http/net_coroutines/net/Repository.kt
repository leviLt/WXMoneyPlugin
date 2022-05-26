package com.wechatplugin.scropio.wxmoneyplugin.http.net_coroutines.net

import com.wechatplugin.scropio.wxmoneyplugin.http.net_coroutines.response.ResultResponse
import kotlinx.coroutines.flow.Flow


interface Repository {

    suspend fun getAction(params:String): Flow<ResultResponse<Any>>
}