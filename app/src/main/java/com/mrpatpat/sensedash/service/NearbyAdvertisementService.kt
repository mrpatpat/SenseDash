package com.mrpatpat.sensedash.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import java.nio.charset.StandardCharsets

enum class AdvertisementConnectionState {
    DISCONNECTED,
    ADVERTISING,
    ACCEPTING,
    CONNECTED
}

class NearbyAdvertisementServiceBinder(private val nearbyAdvertisementService: NearbyAdvertisementService) : Binder() {
    fun getService(): NearbyAdvertisementService = nearbyAdvertisementService
}

class NearbyAdvertisementRepository(private val nearbyAdvertisementService: NearbyAdvertisementService) {
    fun getConnectionState(): LiveData<AdvertisementConnectionState> = nearbyAdvertisementService.state
}

class NearbyAdvertisementService : LifecycleService() {

    private val tag = "NEARBY_ADVERTISEMENT_SERVICE"
    private val service = "ECHO_SERVICE"
    private val user = "HOSTUSER"
    private var connectedEndpoint = ""

    private val connectionsClient: ConnectionsClient by lazy { Nearby.getConnectionsClient(this) }

    val state: MutableLiveData<AdvertisementConnectionState> = MutableLiveData()
    val msg: MutableLiveData<String> = MutableLiveData()

    override fun onCreate() {
        super.onCreate()
        state.value = AdvertisementConnectionState.DISCONNECTED
    }

    fun connect() {
        reconnect()
    }

    fun disconnect() {
        state.value = AdvertisementConnectionState.DISCONNECTED
        connectionsClient.stopAllEndpoints()
        connectionsClient.stopAdvertising()
    }

    private fun reconnect() {
        disconnect()
        state.value = AdvertisementConnectionState.ADVERTISING
        Log.i(tag , state.toString())
        connectionsClient
            .startAdvertising(
                user,
                service,
                connectionLifecycleCallback,
                AdvertisingOptions(Strategy.P2P_STAR)
            )
            .addOnFailureListener { reconnect() }
            .addOnFailureListener { e -> Log.e(tag, e.toString()) }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            state.value = AdvertisementConnectionState.ACCEPTING
            connectionsClient
                .acceptConnection(
                    endpointId,
                    payloadCallback
                )
                .addOnFailureListener { reconnect() }
                .addOnFailureListener { e -> Log.e(tag, e.toString()) }

        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if(result.status.isSuccess) {
                connectedEndpoint = endpointId
                state.value = AdvertisementConnectionState.CONNECTED
                connectionsClient.stopAdvertising()
            } else {
                reconnect()
            }
        }

        override fun onDisconnected(endpointId: String) {
            reconnect()
        }
    }

    fun sendMessage(msg: String) {
        connectionsClient.sendPayload(connectedEndpoint, Payload.fromBytes(msg.toByteArray(StandardCharsets.UTF_8)))
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            msg.value = payload.asBytes()?.toString(StandardCharsets.UTF_8)
        }
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate?) {}
    }
    
    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return NearbyAdvertisementServiceBinder(this)
    }

}
