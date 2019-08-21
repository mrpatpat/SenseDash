package com.mrpatpat.sensedash

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.ActivityCompat
import com.mrpatpat.sensedash.component.debug.DebugActivity
import com.mrpatpat.sensedash.component.debug.debugModule
import com.mrpatpat.sensedash.repository.repositoryModule
import com.mrpatpat.sensedash.service.NearbyAdvertisementService
import com.mrpatpat.sensedash.service.NearbyAdvertisementServiceBinder
import com.mrpatpat.sensedash.service.NearbyDiscoveryService
import com.mrpatpat.sensedash.service.NearbyDiscoveryServiceBinder
import com.mrpatpat.sensedash.widget.gauge.value.ValueGaugeDebugActivity
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SplashActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private var nearbyAdvertisementService: NearbyAdvertisementService? = null
    private var nearbyDiscoveryService: NearbyDiscoveryService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        requestPermissions()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        startServices()
    }

    private fun startServices() {
        initNearbyAdvertisementService()
        initNearbyDiscoveryService()
    }

    private fun onServiceBindingFinished() {
        initDependencyInjection()
        openDebugActivity()
    }

    private fun initDependencyInjection() {
        val koinApplication = startKoin {
            androidContext(applicationContext)
            modules(listOf(
                debugModule,
                repositoryModule
            ))
        }
        koinApplication.koin.declare(nearbyDiscoveryService)
        koinApplication.koin.declare(nearbyAdvertisementService)
    }

    private fun openDebugActivity() {
        val intent = Intent(this, ValueGaugeDebugActivity::class.java)
        startActivity(intent)
    }

    private fun initNearbyAdvertisementService() {
        Intent(this, NearbyAdvertisementService::class.java).also { intent ->
            applicationContext.bindService(intent, object : ServiceConnection {
                override fun onServiceConnected(className: ComponentName, service: IBinder) {
                    val binder = service as NearbyAdvertisementServiceBinder
                    nearbyAdvertisementService = binder.getService()
                    onServiceInitialized()
                }
                override fun onServiceDisconnected(arg0: ComponentName) {}
            }, Context.BIND_AUTO_CREATE)
        }
    }

    private fun initNearbyDiscoveryService() {
        Intent(this, NearbyDiscoveryService::class.java).also { intent ->
            applicationContext.bindService(intent, object : ServiceConnection {
                override fun onServiceConnected(className: ComponentName, service: IBinder) {
                    val binder = service as NearbyDiscoveryServiceBinder
                    nearbyDiscoveryService = binder.getService()
                    onServiceInitialized()
                }
                override fun onServiceDisconnected(arg0: ComponentName) {}
            }, Context.BIND_AUTO_CREATE)
        }
    }

    private fun onServiceInitialized() {
        if(nearbyDiscoveryService != null
            && nearbyAdvertisementService != null) {
            this.onServiceBindingFinished()
        }
    }

}
