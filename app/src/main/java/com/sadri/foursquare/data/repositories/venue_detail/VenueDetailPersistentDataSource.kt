package com.sadri.foursquare.data.repositories.venue_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.sadri.foursquare.data.persistent.venue_detail.VenueDetailDao
import com.sadri.foursquare.data.utils.Result
import com.sadri.foursquare.models.venue.detail.VenueDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Sepehr Sadri on 6/6/2020.
 * sepehrsadri@gmail.com
 * Tehran, Iran.
 * Copyright © 2020 by Sepehr Sadri. All rights reserved.
 */
@Singleton
class VenueDetailPersistentDataSource @Inject constructor(
    val venueDetailDao: VenueDetailDao
) {
    suspend fun getByIdInstantly(id: String): Result<VenueDetail> =
        withContext(Dispatchers.IO) {
            val data = venueDetailDao.getByIdInstantly(id)
            convertToResult(data)
        }

    fun getById(id: String): LiveData<Result<VenueDetail>> =
        liveData(Dispatchers.Main) {
            val source = venueDetailDao.getById(id).map {
                convertToResult(it)
            }
            emitSource(source)
        }

    private fun convertToResult(venueDetail: VenueDetail?): Result<VenueDetail> {
        return if (venueDetail != null) {
            Result.Success(venueDetail)
        } else {
            Result.Empty
        }
    }

    suspend fun clear() =
        withContext(Dispatchers.IO) {
            venueDetailDao.clear()
        }

    suspend fun save(venueDetail: VenueDetail) =
        withContext(Dispatchers.IO) {
            venueDetailDao.insert(venueDetail)
        }
}