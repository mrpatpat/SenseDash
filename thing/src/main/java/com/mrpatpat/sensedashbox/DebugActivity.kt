package com.mrpatpat.sensedashbox

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.mrpatpat.sensedashcore.connection.AppConnectionBinder
import com.mrpatpat.sensedashcore.connection.AppConnectionService

class DebugActivity : Activity() {

    private lateinit var mService: AppConnectionService

    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as AppConnectionBinder
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
        Intent(this, com.mrpatpat.sensedashcore.connection.AppConnectionService::class.java).also { intent ->
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
    }

}
