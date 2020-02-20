/*
 * Copyright © 2019, Oracle and/or its affiliates. All rights reserved.
 */

package cz.lsrom.webviewtest.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cz.lsrom.webviewtest.R

internal class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
    }
}
