package com.sadri.foursquare.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Sepehr Sadri on 6/3/2020.
 * sepehrsadri@gmail.com
 * Tehran, Iran.
 * Copyright © 2020 by Sepehr Sadri. All rights reserved.
 */
data class MyPoint(
    @Expose
    @SerializedName("lat")
    val lat: Double,
    @Expose
    @SerializedName("lng")
    val lng: Double
) {
    override fun toString(): String {
        return "$lat,$lng"
    }
}