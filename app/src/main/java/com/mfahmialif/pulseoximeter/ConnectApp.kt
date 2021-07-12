package com.mfahmialif.pulseoximeter

import android.app.Application

class ConnectApp : Application(){
    override fun onCreate() {
        super.onCreate()
        instance = this

    }

    fun setConnectionListener(listener: ConnectionReceiver.ConnectionReceiverListener){
        ConnectionReceiver.connectionReceiverListener = listener
    }

    companion object{
        @get:Synchronized
        lateinit var instance : ConnectApp
    }
}