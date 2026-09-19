package com.mostafa.majiddelbandam

import android.app.Application
import com.mostafa.majiddelbandam.di.AppContainer

class MajidApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
