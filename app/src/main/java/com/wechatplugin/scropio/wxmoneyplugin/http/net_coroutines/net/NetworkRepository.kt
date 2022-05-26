package com.wechatplugin.scropio.wxmoneyplugin.http.net_coroutines.net

import com.wechatplugin.scropio.wxmoneyplugin.http.net_coroutines.Api
import com.wechatplugin.scropio.wxmoneyplugin.http.net_coroutines.NetworkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetworkRepository {

    @Singleton
    @Provides
    fun provideTasksRepository(
        @Api networkApi: NetworkApi
    ): Repository {
        return NetworkFactory.createRepository(networkApi)
    }
}


object NetworkFactory {
    fun createRepository(networkApi: NetworkApi): Repository {
        return RepositoryImpl(networkApi)
    }
}