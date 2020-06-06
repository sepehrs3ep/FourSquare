package com.sadri.foursquare.ui.screens.dashboard.fragments.dashboard.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sadri.foursquare.R
import com.sadri.foursquare.components.permission.PermissionProvider
import com.sadri.foursquare.ui.navigation.NavigationFragment
import com.sadri.foursquare.ui.navigation.NavigationViewModel
import com.sadri.foursquare.ui.utils.EndlessRecyclerOnScrollListener
import com.sadri.foursquare.ui.utils.snackBar
import com.sadri.foursquare.utils.network.ConnectionStateMonitor
import kotlinx.android.synthetic.main.fragment_dashboard.*
import javax.inject.Inject

/**
 * Created by Sepehr Sadri on 5/31/2020.
 * sepehrsadri@gmail.com
 * Tehran, Iran.
 * Copyright © 2020 by Sepehr Sadri. All rights reserved.
 */
class DashboardFragment : NavigationFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var permissionProvider: PermissionProvider

    @Inject
    lateinit var connectionStateMonitor: ConnectionStateMonitor

    private val viewModel: DashboardViewModel by viewModels { viewModelFactory }
    override fun getViewModel(): NavigationViewModel = viewModel

    private val recyclerViewScrollViewListener =
        object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                viewModel.onScroll()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshExplores()
            swipeRefreshLayout.isRefreshing = false
        }

        connectionStateMonitor.hasInternet.observe(
            viewLifecycleOwner,
            Observer {
                val visibility = if (it) View.GONE else View.VISIBLE
                noInternetRoot.visibility = visibility
            }
        )

        crossIv.setOnClickListener {
            noInternetRoot.visibility = View.GONE
        }

        viewModel.messageEvent.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    requireContext().snackBar(
                        it,
                        container
                    )
                }
            }
        )

        viewModel.locationChange.observe(
            viewLifecycleOwner,
            Observer {
                requireContext().snackBar(
                    R.string.location_updating,
                    container
                )
            }
        )

        viewModel.venuesListAvailability.observe(
            viewLifecycleOwner,
            Observer {
                if (it) {
                    makeListViewVisible()
                } else {
                    makeListViewGone()
                }
            }
        )

        val adapter =
            DashboardListAdapter(
                viewModel
            )

        viewModel.venues.observe(
            viewLifecycleOwner,
            Observer {
                adapter.submitList(it)
            }
        )

        with(rcvVenues) {
            this.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            this.adapter = adapter
        }
    }

    private fun makeListViewVisible() {
        if (rcvVenues.isVisible.not() || noLocationView.isVisible) {
            noLocationView.visibility = View.GONE
            rcvVenues.visibility = View.VISIBLE
        }
    }

    private fun makeListViewGone() {
        if (noLocationView.isVisible.not() || rcvVenues.isVisible) {
            noLocationView.visibility = View.VISIBLE
            rcvVenues.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        rcvVenues.addOnScrollListener(recyclerViewScrollViewListener)
        if (permissionProvider.isLocationAvailableAndAccessible()) {
            viewModel.initFetch()
        }
    }

    override fun onStop() {
        rcvVenues.removeOnScrollListener(recyclerViewScrollViewListener)
        super.onStop()
    }
}