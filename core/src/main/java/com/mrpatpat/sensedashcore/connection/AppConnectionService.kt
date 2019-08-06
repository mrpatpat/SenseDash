package com.mrpatpat.sensedashcore.connection

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class AppConnectionService : Service() {

    private var isAdvertising = false

    private val payloadCallback = object : PayloadCallback() {

        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.i("AppConnectionService", "payload received on endpoint $endpointId")
        }

        override fun onPayloadTransferUpdate(endpointId: String?, update: PayloadTransferUpdate?) {
            Log.i("AppConnectionService", "payload update received on endpoint $endpointId")
        }

    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {

        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.i("AppConnectionService", "app connection initiated on endpoint $endpointId")
            Nearby.getConnectionsClient(this@AppConnectionService).acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            Log.i("AppConnectionService", "app connection status on endpoint $endpointId is ${result.status.statusCode}")
        }

        override fun onDisconnected(endpointId: String) {
            Log.i("AppConnectionService", "app connection closed on endpoint $endpointId")
        }
    }

    private fun onAdvertisingSuccessful() {
        isAdvertising = true
        Log.i("AppConnectionService", "advertising service OK")
    }

    private fun onAdvertisingFailed(e: Exception) {
        isAdvertising = false
        Log.e("AppConnectionService", "advertising service FAIL", e)
    }

    override fun onBind(intent: Intent): IBinder {
        if(!isAdvertising) {
            startAdvertising()
        }
        return AppConnectionBinder(this)
    }

    private fun startAdvertising() {
        val userName = "sensedashbox"
        val serviceId = "sensedashbox"
        val advertisingOptions = AdvertisingOptions(Strategy.P2P_STAR)

        Nearby
            .getConnectionsClient(this)
            .startAdvertising(userName, serviceId, connectionLifecycleCallback, advertisingOptions)
            .addOnSuccessListener { unused: Void -> onAdvertisingSuccessful() }
            .addOnFailureListener { e: Exception -> onAdvertisingFailed(e) }
    }

}
