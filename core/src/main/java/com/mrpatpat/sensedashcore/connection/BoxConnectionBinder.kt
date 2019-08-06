package com.mrpatpat.sensedashcore.connection

import android.os.Binder

class BoxConnectionBinder(private val boxConnectionService: BoxConnectionService) : Binder() {
    fun getService(): BoxConnectionService = boxConnectionService
}