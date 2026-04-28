package com.rajarsheechatterjee.NativeDownloadManager

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.lnreader.spec.NativeDownloadManagerSpec

class NativeDownloadManager(private val reactContext: ReactApplicationContext) : NativeDownloadManagerSpec(reactContext) {
    @ReactMethod
    override fun downloadApk(url: String, title: String, description: String, promise: Promise) {
        try {
            val downloadManager = reactContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(url)
            val request = DownloadManager.Request(uri)

            request.setTitle(title)
            request.setDescription(description)
            request.setMimeType("application/vnd.android.package-archive")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            
            // Extract filename from URL or fallback
            var fileName = url.substringAfterLast("/")
            if (!fileName.endsWith(".apk")) {
                fileName = "lnreader-update.apk"
            }
            
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

            downloadManager.enqueue(request)
            promise.resolve(null)
        } catch (e: Exception) {
            promise.reject("DOWNLOAD_ERROR", "Failed to start download", e)
        }
    }
}
