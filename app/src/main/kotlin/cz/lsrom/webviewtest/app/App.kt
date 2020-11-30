package cz.lsrom.webviewtest.app

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import cz.lsrom.webviewtest.BuildConfig
import timber.log.Timber

internal class App : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseCrashlytics
            .getInstance()
            .setCrashlyticsCollectionEnabled(BuildConfig.DEBUG.not())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
