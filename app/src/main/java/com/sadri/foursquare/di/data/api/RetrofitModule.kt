package com.sadri.foursquare.di.data.api

import com.sadri.foursquare.data.api.RetrofitProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

/**
 * Created by Sepehr Sadri on 5/31/2020.
 * sepehrsadri@gmail.com
 * Tehran, Iran.
 * Copyright © 2020 by Sepehr Sadri. All rights reserved.
 */
@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    fun provideRetrofit(): Retrofit {
        return RetrofitProvider.getRetrofit()
    }
}