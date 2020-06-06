package com.sadri.foursquare.ui.screens.dashboard.fragments.dashboard.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.sadri.foursquare.components.location.LocationProvider
import com.sadri.foursquare.components.permission.PermissionProvider
import com.sadri.foursquare.data.repositories.explore.EXPLORE_DATA_OFFSET
import com.sadri.foursquare.data.repositories.explore.EXPLORE_MAXIMUM_PAGE
import com.sadri.foursquare.data.repositories.explore.ExploreDataSingleSourceOfTruth
import com.sadri.foursquare.data.repositories.explore.ExploreResult
import com.sadri.foursquare.data.repositories.venue_detail.VenueDetailSingleSourceOfTruth
import com.sadri.foursquare.data.utils.Result
import com.sadri.foursquare.models.venue.Venue
import com.sadri.foursquare.ui.navigation.NavigationViewModel
import com.sadri.foursquare.utils.gps.GpsStateMonitor
import com.sadri.foursquare.utils.live_data.SingleLiveEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Sepehr Sadri on 5/31/2020.
 * sepehrsadri@gmail.com
 * Tehran, Iran.
 * Copyright © 2020 by Sepehr Sadri. All rights reserved.
 */
class DashboardViewModel @Inject constructor(
    gpsStateMonitor: GpsStateMonitor,
    private val permissionProvider: PermissionProvider,
    private val exploreDataSingleSourceOfTruth: ExploreDataSingleSourceOfTruth,
    private val venueDetailSingleSourceOfTruth: VenueDetailSingleSourceOfTruth,
    locationProvider: LocationProvider
) : NavigationViewModel() {
    val messageEvent = SingleLiveEvent<String>()
    val locationChange = SingleLiveEvent<Nothing>()

    private val _venues = MutableLiveData<List<Venue>>()
    val venues: LiveData<List<Venue>>
        get() = _venues

    private val _venuesListAvailability = MutableLiveData(true)
    val venuesListAvailability: LiveData<Boolean>
        get() = _venuesListAvailability

    private var page: Int = 0

    private val venuesList: MutableList<Venue> = ArrayList()

    private var isLoading = false

    private val exploreObserver =
        Observer<Result<ExploreResult>> {
            when (it) {
                is Result.Loading -> {
                    fullscreenLoading.value = true
                }
                is Result.Success -> {
                    _venuesListAvailability.value = true
                    updateVenuesList(it)
                    fullscreenLoading.value = false
                    isLoading = false
                }
                is Result.Error -> {
                    fullscreenLoading.value = false
                    messageEvent.value = it.error.message
                    isLoading = false

                    if (venuesList.isEmpty()) {
                        _venuesListAvailability.value = false
                    }
                }
            }
        }

    private fun updateVenuesList(it: Result.Success<ExploreResult>) {
        val data = it.data
        val venues = data.result
        val offset = data.offset
        val reliable = data.reliable

        if (reliable && venuesList.size >= offset + EXPLORE_DATA_OFFSET) {

            for ((index, i) in
            (offset until offset + EXPLORE_DATA_OFFSET).withIndex()) {
                venuesList[i] = venues[index]
            }
        } else {
            venuesList.addAll(venues)
        }

        _venues.value = ArrayList(venuesList.toList())
    }

    init {
        observeWithInitUpdate(
            gpsStateMonitor.hasGps,
            Observer {
                if (permissionProvider.isLocationAvailableAndAccessible().not()) {
                    navigate(DashboardFragmentDirections.navigateToRequirementSatisfierFragment())
                }
            }
        )
        observe(
            locationProvider.locationChange,
            Observer {
                locationChange.call()
                venuesList.clear()
                page = 0
                fetchVenues()
                invalidateVenueDetailSource()
            }
        )
    }

    private fun invalidateVenueDetailSource() {
        viewModelScope.launch {
            venueDetailSingleSourceOfTruth
                .venueDetailPersistentDataSource
                .clear()
        }
    }

    fun onVenueClick(venue: Venue) {
        navigate(
            DashboardFragmentDirections
                .navigateToVenueDetailFragment(
                    venue.id
                )
        )
    }

    fun onScroll() {
        page++
        fetchVenues()
    }

    private fun fetchVenues() {
        if (isLoading || isReachMaximumPage()) {
            return
        }
        isLoading = true

        observe(
            exploreDataSingleSourceOfTruth.fetchExplores(
                page * EXPLORE_DATA_OFFSET
            ),
            exploreObserver
        )
    }

    private fun isReachMaximumPage() = page > EXPLORE_MAXIMUM_PAGE

    fun initFetch() {
        if (venuesList.isEmpty()) {
            fetchVenues()
        }
    }

    fun refreshExplores() {
        viewModelScope.launch {
            exploreDataSingleSourceOfTruth.explorePersistentDataSource.clear()
            venuesList.clear()
            page = 0
            fetchVenues()
        }
    }
}