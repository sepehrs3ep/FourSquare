package com.sadri.foursquare.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

/**
 * Created by Sepehr Sadri on 6/3/2020.
 * sepehrsadri@gmail.com
 * Tehran, Iran.
 * Copyright © 2020 by Sepehr Sadri. All rights reserved.
 */
/**
 * Abstract Base Data source class with error handling
 */
abstract class BaseAPIDataSource(private val apiErrorHandler: ApiErrorHandler) {
    protected suspend fun <T> getResult(call: suspend () -> Response<T>): ApiResult<T> =
        withContext(Dispatchers.IO) {
            try {
                val response = call()
                if (response.isSuccessful) {
                    ApiResult.Success(
                        response.body()
                    )
                } else {
                    apiErrorHandler.onError(response)
                }
            } catch (e: Exception) {
                Timber.e(e)
                apiErrorHandler.onFailed(e)
            }
        }
}