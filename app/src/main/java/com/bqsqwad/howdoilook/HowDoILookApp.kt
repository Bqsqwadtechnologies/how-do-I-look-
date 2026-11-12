package com.bqsqwad.howdoilook

import android.app.Application
import com.bqsqwad.howdoilook.notifications.HowDoILookNotifications
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class HowDoILookApp : Application() {
    override fun onCreate() {
        super.onCreate()
        HowDoILookNotifications.createChannels(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
