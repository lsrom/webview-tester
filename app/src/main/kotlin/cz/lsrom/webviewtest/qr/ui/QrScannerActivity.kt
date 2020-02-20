/*
 * Copyright © 2019, Oracle and/or its affiliates. All rights reserved.
 */

package cz.lsrom.webviewtest.qr.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.zxing.Result
import cz.lsrom.webviewtest.R
import cz.lsrom.webviewtest.app.MainActivity
import cz.lsrom.webviewtest.webview.platform.WebViewPresenter
import kotlinx.android.synthetic.main.qr_scanner_view.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import timber.log.Timber

class QrScannerActivity : AppCompatActivity(),
    ZXingScannerView.ResultHandler {

    private lateinit var presenter: WebViewPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_scanner_view)

        if (!permissionGranted()) {
            requestPermission()
        }

        request_permission.setOnClickListener {
            requestPermission()
        }
    }

    private fun permissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            666
        )
    }

    public override fun onPause() {
        super.onPause()
        if (permissionGranted()) {
            qr_scanner_view.stopCamera()
        }
    }

    public override fun onResume() {
        super.onResume()
        if (permissionGranted()) {
            qr_scanner_view.setResultHandler(this)
            qr_scanner_view.startCamera()
        }
    }

    override fun handleResult(rawResult: Result) {
        presenter.setUrl(rawResult.text)
        startActivity(Intent(baseContext, MainActivity::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.d("request permission result")
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            request_permission.isVisible = true
            qr_scanner_view.isVisible = false
        } else {
            request_permission.isVisible = false
            qr_scanner_view.isVisible = true
            qr_scanner_view.startCamera()
        }
    }
}
