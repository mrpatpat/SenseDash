package com.mrpatpat.sensedash.repository

import androidx.lifecycle.LiveData
import com.mrpatpat.sensedash.service.AdvertisementConnectionState
import com.mrpatpat.sensedash.service.DiscoveryConnectionState
import com.mrpatpat.sensedash.service.NearbyAdvertisementService
import com.mrpatpat.sensedash.service.NearbyDiscoveryService

class AdvertisementStateRepository(advertisementService: NearbyAdvertisementService) {
    val state: LiveData<AdvertisementConnectionState> = advertisementService.state
}