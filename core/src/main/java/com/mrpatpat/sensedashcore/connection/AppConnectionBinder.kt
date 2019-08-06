package com.mrpatpat.sensedashcore.connection

import android.os.Binder

class AppConnectionBinder(private val appConnectionService: AppConnectionService) : Binder() {
    fun getService(): AppConnectionService = appConnectionService
}