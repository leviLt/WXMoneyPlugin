package com.wechatplugin.scropio.wxmoneyplugin.http.net_coroutines.net

import com.wechatplugin.scropio.wxmoneyplugin.http.net_coroutines.NetworkApi
import com.wechatplugin.scropio.wxmoneyplugin.http.net_coroutines.response.ResultResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class RepositoryImpl(private val networkApi: NetworkApi) : Repository {

    /**
     * 获取动作
     */
    override suspend fun getAction(params: String): Flow<ResultResponse<Any>> {
        return flow {
            emit(networkApi.getAction(params))
        }.flowOn(Dispatchers.IO)
    }
}