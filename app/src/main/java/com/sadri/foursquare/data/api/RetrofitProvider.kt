package com.sadri.foursquare.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Sepehr Sadri on 5/31/2020.
 * sepehrsadri@gmail.com
 * Tehran, Iran.
 * Copyright © 2020 by Sepehr Sadri. All rights reserved.
 */
object RetrofitProvider {
    private const val TIMEOUT_SECONDS: Long = 15

    val CLIENT by lazy {
        val httpClient = OkHttpClient.Builder()
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)

        APIUtils.getLoggingInterceptor()?.let {
            httpClient.addInterceptor(
                it
            )
        }

        httpClient.build()
    }

    fun getRetrofit(): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)

        APIUtils.getLoggingInterceptor()?.let {
            httpClient.addInterceptor(
                it
            )
        }

        val builder = Retrofit.Builder()
            .baseUrl(
                APIUtils.API_BASE_URL
            ).addConverterFactory(
                GsonConverterFactory.create()
            )
            .client(
                CLIENT
            )

        return builder.build()
    }

    fun <T> provideService(
        retrofit: Retrofit,
        clazz: Class<T>
    ): T {
        return retrofit.create(clazz)
    }
}