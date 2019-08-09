package com.mrpatpat.sensedash

import androidx.lifecycle.*

class DebugViewModel: ViewModel() {
    val nearbyDiscoveryMessages: MutableLiveData<String> = MutableLiveData()
    val nearbyDiscoveryConnectionState: MutableLiveData<DiscoveryConnectionState> = MutableLiveData()
    val nearbyDiscoveryConnectionProgressMax = 4
    val nearbyDiscoveryConnectionProgressMin = 0

    val nearbyAdvertisementMessages: MutableLiveData<String> = MutableLiveData()
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

    fun getMessageLog() : LiveData<String> {
        val discovery = Transformations.map(nearbyDiscoveryMessages) {
            "D: $it"
        }
        val advertisement = Transformations.map(nearbyAdvertisementMessages) {
            "A: $it"
        }
        val merged = MediatorLiveData<String>()
        merged.addSource(discovery) { value -> merged.setValue(value) }
        merged.addSource(advertisement) { value -> merged.setValue(value) }
        return merged
    }

}