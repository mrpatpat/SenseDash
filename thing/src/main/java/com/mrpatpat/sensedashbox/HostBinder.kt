package com.mrpatpat.sensedashbox

import android.os.Binder

class HostBinder(private val hostService: HostService) : Binder() {
    fun getService(): HostService = hostService
}