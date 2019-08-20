package com.mrpatpat.sensedash.repository

import androidx.lifecycle.LiveData
import com.mrpatpat.sensedash.service.DiscoveryConnectionState
import com.mrpatpat.sensedash.service.NearbyDiscoveryService

class DiscoveryStateRepository(nearbyDiscoveryService: NearbyDiscoveryService) {
    val state: LiveData<DiscoveryConnectionState> = nearbyDiscoveryService.state
}