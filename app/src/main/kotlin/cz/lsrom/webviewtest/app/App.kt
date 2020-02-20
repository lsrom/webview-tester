/*
 * Copyright © 2019, Oracle and/or its affiliates. All rights reserved.
 */

package cz.lsrom.webviewtest.app

import android.app.Application
import cz.lsrom.webviewtest.BuildConfig
import timber.log.Timber

internal class App: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
}
