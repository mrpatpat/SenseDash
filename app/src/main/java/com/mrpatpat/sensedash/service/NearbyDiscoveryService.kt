package com.mrpatpat.sensedash.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

enum class DiscoveryConnectionState {
    DISCONNECTED,
    DISCOVERING,
    REQUESTING,
    ACCEPTING,
    CONNECTED
}

class NearbyDiscoveryServiceBinder(private val nearbyDiscoveryService: NearbyDiscoveryService) : Binder() {
    fun getService(): NearbyDiscoveryService = nearbyDiscoveryService
}

class NearbyDiscoveryService : LifecycleService() {

    private val tag = "NEARBY_DISCOVERY_SERVICE"
    private val service = "ECHO_SERVICE"
    private val user = "CLIENTUSER"

    private var connectedEndpoint = ""

    private val connectionsClient: ConnectionsClient by lazy { Nearby.getConnectionsClient(this) }

    val state: MutableLiveData<DiscoveryConnectionState> = MutableLiveData()
    val payload: MutableLiveData<Payload> = MutableLiveData()
    val progress: MutableLiveData<PayloadTransferUpdate> = MutableLiveData()

    override fun onCreate() {
        super.onCreate()
        state.value = DiscoveryConnectionState.DISCONNECTED
    }

    fun connect() {
        reconnect()
    }

    fun disconnect() {
        state.value = DiscoveryConnectionState.DISCONNECTED
        connectionsClient.stopAllEndpoints()
        connectionsClient.stopDiscovery()
    }

    private fun reconnect() {
        disconnect()
        state.value = DiscoveryConnectionState.DISCOVERING
        Log.i(tag , state.toString())
        connectionsClient
            .startDiscovery(
                service,
                endpointDiscoveryCallback,
                DiscoveryOptions(Strategy.P2P_STAR)
            )
            .addOnFailureListener { reconnect() }
            .addOnFailureListener { e -> Log.e(tag, e.toString()) }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            state.value = DiscoveryConnectionState.REQUESTING
            connectionsClient
                .requestConnection(
                    user,
                    endpointId,
                    connectionLifecycleCallback
                )
                .addOnFailureListener { reconnect() }
                .addOnFailureListener { e -> Log.e(tag, e.toString()) }

        }

        override fun onEndpointLost(endpointId: String) {
            reconnect()
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            state.value = DiscoveryConnectionState.ACCEPTING
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
                state.value = DiscoveryConnectionState.CONNECTED
                connectionsClient.stopDiscovery()
            } else {
                reconnect()
            }
        }

        override fun onDisconnected(endpointId: String) {
            reconnect()
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            this@NearbyDiscoveryService.payload.value = payload
        }
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate?) {
            this@NearbyDiscoveryService.progress.value = update
        }
    }
    
    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return NearbyDiscoveryServiceBinder(this)
    }

    fun sendPayload(payload: Payload) {
        this.connectionsClient.sendPayload(connectedEndpoint, payload);
    }

}
