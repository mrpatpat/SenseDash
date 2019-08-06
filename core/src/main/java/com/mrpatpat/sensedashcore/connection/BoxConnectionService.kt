package com.mrpatpat.sensedashcore.connection

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class BoxConnectionService : Service() {

    private var hasDiscovered = false

    private val payloadCallback = object : PayloadCallback() {

        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.i("BoxConnectionService", "payload received on endpoint $endpointId")
        }

        override fun onPayloadTransferUpdate(endpointId: String?, update: PayloadTransferUpdate?) {
            Log.i("BoxConnectionService", "payload update received on endpoint $endpointId")
        }

    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {

        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Nearby.getConnectionsClient(this@BoxConnectionService)
                .requestConnection("SomeNickname", endpointId, connectionLifecycleCallback)
                .addOnSuccessListener { unused: Void -> Log.i("BoxConnectionService", "box connection requested on endpoint $endpointId")}
                .addOnFailureListener { e: Exception ->  Log.e("BoxConnectionService", "box connection request on endpoint $endpointId failed", e)}
        }

        override fun onEndpointLost(endpointId: String) {
            Log.i("BoxConnectionService", "box connection endpoint $endpointId lost")
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.i("BoxConnectionService", "box connection initiated on endpoint $endpointId")
            Nearby.getConnectionsClient(this@BoxConnectionService).acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            Log.i("BoxConnectionService", "box connection status on endpoint $endpointId is ${result.status.statusCode}")
        }

        override fun onDisconnected(endpointId: String) {
            Log.i("BoxConnectionService", "box connection closed on endpoint $endpointId")
        }
    }

    private fun onDiscoverySuccessful() {
        hasDiscovered = true
        Log.i("BoxConnectionService", "discovered service")
    }

    private fun onDiscoveryFailed(e: Exception) {
        hasDiscovered = false
        Log.e("BoxConnectionService", "discovering service failed", e)
    }

    override fun onBind(intent: Intent): IBinder {
        if(!hasDiscovered) {
            startDiscovering()
        }
        return BoxConnectionBinder(this)
    }

    private fun startDiscovering() {
        val serviceId = "sensedashbox"
        val discoveryOptions = DiscoveryOptions(Strategy.P2P_STAR)

        Nearby
            .getConnectionsClient(this)
            .startDiscovery(serviceId, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener { unused: Void -> onDiscoverySuccessful() }
            .addOnFailureListener { e: Exception -> onDiscoveryFailed(e) }
    }

}
