package com.mrpatpat.sensedashbox

import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import android.os.Handler


enum class ConnectionState {
    DISCONNECTED,
    ADVERTISING,
    ACCEPTING,
    CONNECTED
}

class HostService : LifecycleService() {

    private var startedLooping = false
    private val loopingHandler = Handler()

    private val runnable = Runnable {
        if (startedLooping) {
            if(state.value == ConnectionState.DISCONNECTED) {
                startAdvertising()
            } else {
                Log.i(tag , "Not yet disconnected, no need to retry advertisement")
            }
            startLoopingAdvertise()
        }
    }

    fun stopLoopingAdvertise() {
        startedLooping = false
        loopingHandler.removeCallbacks(runnable)
    }

    fun startLoopingAdvertise() {
        startedLooping = true
        loopingHandler.postDelayed(runnable, 3000)
    }

    private val tag = "HOST"
    private val service = "ECHO_SERVICE"
    private val endpoint = "ECHO"
    private val user = "HOSTUSER"

    private val connectionsClient by lazy { Nearby.getConnectionsClient(this@HostService) }

    val state: MutableLiveData<ConnectionState> = MutableLiveData()

    override fun onCreate() {
        super.onCreate()
        state.value = ConnectionState.DISCONNECTED
        startLoopingAdvertise()
        state.observe(this, Observer { x -> onStateChange(x) })
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLoopingAdvertise()
    }

    private fun onStateChange(state: ConnectionState) {
        Log.i(tag , state.toString())
    }

    fun startAdvertising() {
        state.value = ConnectionState.ADVERTISING

        connectionsClient
            .stopAdvertising()

        connectionsClient
            .stopAllEndpoints()

        connectionsClient
            .startAdvertising(
                user,
                service,
                connectionLifecycleCallback,
                AdvertisingOptions(Strategy.P2P_STAR)
            )
            .addOnFailureListener { state.value = ConnectionState.DISCONNECTED }
            .addOnFailureListener { e -> Log.e(tag, e.toString()) }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            state.value = ConnectionState.ACCEPTING
            connectionsClient
                .acceptConnection(
                    endpointId,
                    payloadCallback
                )
                .addOnFailureListener { state.value = ConnectionState.DISCONNECTED }
                .addOnFailureListener { e -> Log.e(tag, e.toString()) }

        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if(result.status.isSuccess) {
                connectionsClient
                    .stopAdvertising()

                state.value = ConnectionState.CONNECTED
            } else {
                state.value = ConnectionState.DISCONNECTED
            }
        }

        override fun onDisconnected(endpointId: String) {
            state.value = ConnectionState.DISCONNECTED
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {}
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate?) {}
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return HostBinder(this)
    }

}
