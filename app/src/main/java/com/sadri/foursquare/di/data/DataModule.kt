package com.sadri.foursquare.di.data

import com.sadri.foursquare.di.data.api.RetrofitModule
import dagger.Module

/**
 * Created by Sepehr Sadri on 5/31/2020.
 * sepehrsadri@gmail.com
 * Tehran, Iran.
 * Copyright © 2020 by Sepehr Sadri. All rights reserved.
 */
@Module(
    includes = [
        RetrofitModule::class
    ]
)
object DataModule