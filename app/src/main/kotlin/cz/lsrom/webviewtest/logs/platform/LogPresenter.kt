/*
 * Copyright © 2019, Oracle and/or its affiliates. All rights reserved.
 */

package cz.lsrom.webviewtest.logs.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import cz.lsrom.webviewtest.R
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val NEW_LINE = "\n\n"

internal class LogPresenter: ViewModel() {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val logs = mutableListOf<String>()

    fun addLog(log: String) = logs.add(log)

    fun clearLogs() = logs.clear()

    fun getLogsAsText(): String {
        return StringBuilder(10_000)
            .apply {
                logs
                    .apply { reverse() }
                    .map { append(it).append(NEW_LINE) }
            }.toString()
    }

    fun filterLogsToText(searchValue: String): String {
        return StringBuilder()
            .apply {
                logs
                    .filter { it.contains(searchValue) }
                    .map { append(it).append(NEW_LINE) }
            }.toString()
    }

    fun shareLogs(context: Context){
        Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_logs_subject))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_logs_message))
            createLogFile(context)?.let { file -> putExtra(Intent.EXTRA_STREAM, file) }
        }.also {
            context.startActivity(Intent.createChooser(it, context.getString(R.string.share)))
        }
    }

    private fun createLogFile(context: Context): Uri? {
        context
            .getExternalFilesDir(null)
            ?.path
            ?.let { path ->
                val file = File(path, "${dateFormatter.format(Date())}-logs.txt")

                if (!file.exists()){
                    file.createNewFile()
                }

                val outputStreamWriter = FileOutputStream(file)
                try {
                    outputStreamWriter.write(
                        getLogsAsText().toByteArray(charset("UTF-8"))
                    )
                } catch (e: IOException) {
                    Timber.e("UTF-8 not supported.")
                }
                outputStreamWriter.close()

                return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            }

        return null
    }
}
