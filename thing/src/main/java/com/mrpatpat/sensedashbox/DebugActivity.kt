package com.mrpatpat.sensedashbox

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat

class DebugActivity : Activity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var mService: HostService

    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as HostBinder
            mService = binder.getService()
            mBound = true
            Log.i("DebugActivity", "connected to app connection service")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
            Log.i("DebugActivity", "disconnected from app connection service")
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i("DebugActivity", "binding to app connection service")
        Intent(this, HostService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
        Log.i("DebugActivity", "unbinding from app connection service")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        requestPermissions()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
