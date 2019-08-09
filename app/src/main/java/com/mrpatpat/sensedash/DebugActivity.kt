package com.mrpatpat.sensedash

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*

class DebugActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var model: DebugViewModel
    private lateinit var nearbyDiscovery: NearbyDiscoveryService
    private lateinit var nearbyAdvertisement: NearbyAdvertisementService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectViewModel()
        requestPermissions()
    }

    private fun startNearbyServices() {
        Intent(this, NearbyDiscoveryService::class.java).also { intent ->
            applicationContext.bindService(intent, object : ServiceConnection {
                override fun onServiceConnected(className: ComponentName, service: IBinder) {
                    val binder = service as NearbyDiscoveryServiceBinder
                    nearbyDiscovery = binder.getService()
                    connectViewModelToNearbyDiscoveryService()
                }
                override fun onServiceDisconnected(arg0: ComponentName) {}
            }, Context.BIND_AUTO_CREATE)
        }

        Intent(this, NearbyAdvertisementService::class.java).also { intent ->
            applicationContext.bindService(intent, object : ServiceConnection {
                override fun onServiceConnected(className: ComponentName, service: IBinder) {
                    val binder = service as NearbyAdvertisementServiceBinder
                    nearbyAdvertisement = binder.getService()
                    connectViewModelToNearbyAdvertisementService()
                }
                override fun onServiceDisconnected(arg0: ComponentName) {}
            }, Context.BIND_AUTO_CREATE)
        }
    }

    fun onNearbyDiscoveryConnectClick(view: View) {
        nearbyDiscovery.connect()
    }

    fun onNearbyDiscoveryDisconnectClick(view: View) {
        nearbyDiscovery.disconnect()
    }

    fun onNearbyAdvertisementConnectClick(view: View) {
        nearbyAdvertisement.connect()
    }

    fun onNearbyAdvertisementDisconnectClick(view: View) {
        nearbyAdvertisement.disconnect()
    }

    private fun connectViewModelToNearbyDiscoveryService() {
        nearbyDiscovery.state.observe(this@DebugActivity, Observer { x -> model.nearbyDiscoveryConnectionState.value = x })
    }

    private fun connectViewModelToNearbyAdvertisementService() {
        nearbyAdvertisement.state.observe(this@DebugActivity, Observer { x -> model.nearbyAdvertisementConnectionState.value = x })
    }

    private fun connectViewModel() {
        model = ViewModelProviders.of(this)[DebugViewModel::class.java]

        progress_nearby.min = model.nearbyDiscoveryConnectionProgressMin
        progress_nearby.max = model.nearbyDiscoveryConnectionProgressMax
        model.getNearbyDiscoveryConnectionProgress().observe(this, Observer { x -> progress_nearby.setProgress(x, true) })
        model.getNearbyDiscoveryConnectionStateText().observe(this, Observer { x -> tv_nearby.text = x })

        progress_nearby_advertisement.min = model.nearbyAdvertisementConnectionProgressMin
        progress_nearby_advertisement.max = model.nearbyAdvertisementConnectionProgressMax
        model.getNearbyAdvertisementConnectionProgress().observe(this, Observer { x -> progress_nearby_advertisement.setProgress(x, true) })
        model.getNearbyAdvertisementConnectionStateText().observe(this, Observer { x -> tv_nearby_advertisement.text = x })
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        startNearbyServices()
    }
}
