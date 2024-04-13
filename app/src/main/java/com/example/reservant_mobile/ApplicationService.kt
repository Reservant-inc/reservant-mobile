package com.example.reservant_mobile

import android.app.Application

class ApplicationService: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: ApplicationService
            private set
    }
}