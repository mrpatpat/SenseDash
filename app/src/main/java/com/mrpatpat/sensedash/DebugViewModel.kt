package com.mrpatpat.sensedash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class DebugViewModel: ViewModel() {
    val nearbyDiscoveryConnectionState: MutableLiveData<DiscoveryConnectionState> = MutableLiveData()
    val nearbyDiscoveryConnectionProgressMax = 4
    val nearbyDiscoveryConnectionProgressMin = 0

    val nearbyAdvertisementConnectionState: MutableLiveData<AdvertisementConnectionState> = MutableLiveData()
    val nearbyAdvertisementConnectionProgressMax = 3
    val nearbyAdvertisementConnectionProgressMin = 0

    fun getNearbyDiscoveryConnectionProgress() : LiveData<Int> {
        return Transformations.map(nearbyDiscoveryConnectionState) {
            when (it) {
                DiscoveryConnectionState.DISCONNECTED -> 0
                DiscoveryConnectionState.DISCOVERING -> 1
                DiscoveryConnectionState.REQUESTING -> 2
                DiscoveryConnectionState.ACCEPTING -> 3
                DiscoveryConnectionState.CONNECTED -> 4
                else -> 0
            }
        }
    }

    fun getNearbyAdvertisementConnectionProgress() : LiveData<Int> {
        return Transformations.map(nearbyAdvertisementConnectionState) {
            when (it) {
                AdvertisementConnectionState.DISCONNECTED -> 0
                AdvertisementConnectionState.ADVERTISING -> 1
                AdvertisementConnectionState.ACCEPTING -> 2
                AdvertisementConnectionState.CONNECTED -> 3
                else -> 0
            }
        }
    }

    fun getNearbyDiscoveryConnectionStateText() : LiveData<String> {
        return Transformations.map(nearbyDiscoveryConnectionState) {
            it.toString()
        }
    }

    fun getNearbyAdvertisementConnectionStateText() : LiveData<String> {
        return Transformations.map(nearbyAdvertisementConnectionState) {
            it.toString()
        }
    }
}