package com.mrpatpat.sensedash

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.mrpatpat.sensedashcore.connection.BoxConnectionBinder
import com.mrpatpat.sensedashcore.connection.BoxConnectionService

class DebugActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var mService: BoxConnectionService

    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BoxConnectionBinder
            mService = binder.getService()
            mBound = true
            Log.i("DebugActivity", "connected to box connection service")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
            Log.i("DebugActivity", "disconnected from box connection service")
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
        Log.i("DebugActivity", "unbinding from box connection service")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions,0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i("DebugActivity", "binding to box connection service")
        Intent(this, BoxConnectionService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
}
