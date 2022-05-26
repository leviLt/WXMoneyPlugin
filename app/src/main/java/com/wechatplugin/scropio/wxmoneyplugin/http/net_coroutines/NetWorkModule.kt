package com.wechatplugin.scropio.wxmoneyplugin.http.net_coroutines

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetWorkModule {


    @Provides
    @Singleton
    @OKHttpClient
    fun provideOKHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        //添加header
        builder.addInterceptor { chain ->
            val headers = mutableMapOf<String, String>()
//            headers["role-type"] = "USER"
//            headers["token"] = UserRepository.getInstance().getToken()
            val newBuilder = chain.request().newBuilder()
            headers.forEach {
                newBuilder.addHeader(it.key, it.value)
            }
            chain.proceed(newBuilder.build())
        }
        //添加拦截器
        builder.readTimeout(3, TimeUnit.MINUTES)
        builder.connectTimeout(2, TimeUnit.MINUTES)
        builder.writeTimeout(2, TimeUnit.MINUTES)
        return builder.build()
    }


    @Provides
    @Singleton
    @RetrofitAnnotation
    fun provideRetrofit(@OKHttpClient okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://api.yxht.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Api
    fun getNetworkApi(@RetrofitAnnotation retrofit: Retrofit): NetworkApi {
        return retrofit.create(NetworkApi::class.java)
    }

}