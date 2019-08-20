package com.mrpatpat.sensedash.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.nearby.connection.Payload
import com.mrpatpat.sensedash.service.NearbyDiscoveryService
import java.nio.charset.StandardCharsets

class AdvertisementMessageRepository(private val nearbyDiscoveryService: NearbyDiscoveryService) {

    private val msg: MutableLiveData<String> = MutableLiveData()

    init {
        this.nearbyDiscoveryService.payload.observe(nearbyDiscoveryService, Observer { x -> onPayloadReceived(x) })
    }

    fun sendMessage(msg: String) {
        this.nearbyDiscoveryService.sendPayload(Payload.fromBytes(msg.toByteArray(StandardCharsets.UTF_8)))
    }

    fun getMessages():LiveData<String> {
        return this.msg
    }

    private fun onPayloadReceived(payload: Payload) {
        msg.value = payload.asBytes()?.toString(StandardCharsets.UTF_8)
    }

}