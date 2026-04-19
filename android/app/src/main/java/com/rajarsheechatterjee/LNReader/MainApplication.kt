package com.rajarsheechatterjee.LNReader

import android.app.Application
import android.content.res.Configuration
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeApplicationEntryPoint.loadReactNative
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.load
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.soloader.SoLoader
import com.rajarsheechatterjee.NativeFile.NativePackage
import com.rajarsheechatterjee.NativeVolumeButtonListener.NativeVolumeButtonListenerPackage
import com.rajarsheechatterjee.NativeTTSMediaControl.NativeTTSMediaControlPackage
import com.rajarsheechatterjee.NativeZipArchive.NativeZipArchivePackage
import expo.modules.ApplicationLifecycleDispatcher

import com.facebook.react.modules.network.OkHttpClientProvider
import com.facebook.react.modules.network.OkHttpClientFactory
import okhttp3.OkHttpClient
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress
import android.os.Handler
import android.os.Looper

class MainApplication : Application(), ReactApplication {
    override val reactNativeHost: ReactNativeHost =
        object : DefaultReactNativeHost(this) {
            override fun getPackages(): List<ReactPackage> =
                PackageList(this).packages.apply {
                    add(NativePackage())
                    add(NativeTTSMediaControlPackage())
                    add(NativeVolumeButtonListenerPackage())
                    add(NativeZipArchivePackage())
                    add(FlagSecurePackage())
                }

            override fun getJSMainModuleName(): String = "index"

            override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG

            override val isNewArchEnabled: Boolean = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
            override val isHermesEnabled: Boolean = BuildConfig.IS_HERMES_ENABLED
        }

    override val reactHost: ReactHost
        get() = getDefaultReactHost(applicationContext, reactNativeHost)
 
    override fun onCreate() {
        super.onCreate()

        setupCrashHandler()
        setupNetworkClient()

        loadReactNative(this)
        ApplicationLifecycleDispatcher.onApplicationCreate(this)
    }

    private fun setupCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val cacheDir = applicationContext.externalCacheDir ?: applicationContext.cacheDir
                val file = java.io.File(cacheDir, "crash_log.txt")
                val date = java.util.Date()
                val printWriter = java.io.PrintWriter(java.io.FileWriter(file))
                printWriter.println("Date: $date")
                printWriter.println("App Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                printWriter.println("Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
                printWriter.println("Android Version: ${android.os.Build.VERSION.RELEASE} (SDK ${android.os.Build.VERSION.SDK_INT})")
                printWriter.println("--- Stack Trace ---")
                throwable.printStackTrace(printWriter)
                printWriter.flush()
                printWriter.close()
            } catch (e: Exception) {
                // Ignored
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun setupNetworkClient() {
        OkHttpClientProvider.setOkHttpClientFactory(object : OkHttpClientFactory {
            var client: OkHttpClient? = null

            override fun createNewNetworkModuleClient(): OkHttpClient {
                val bootstrapClient = OkHttpClient.Builder().build()
                val dns = DnsOverHttps.Builder().client(bootstrapClient)
                    .url("https://cloudflare-dns.com/dns-query".toHttpUrl())
                    .bootstrapDnsHosts(
                        InetAddress.getByName("1.1.1.1"),
                        InetAddress.getByName("1.0.0.1"),
                        InetAddress.getByName("2606:4700:4700::1111"),
                        InetAddress.getByName("2606:4700:4700::1001")
                    )
                    .build()
                val builder = OkHttpClientProvider.createClientBuilder()
                
                builder.addInterceptor { chain ->
                    val response = chain.proceed(chain.request())
                    if ((response.code == 403 || response.code == 503) && 
                        response.header("Server", "")?.contains("cloudflare", ignoreCase = true) == true) {
                        Thread {
                            Thread.sleep(2000)
                            client?.connectionPool?.evictAll()
                        }.start()
                    }
                    response
                }
                
                builder.dns(dns)
                client = builder.build()
                return client!!
            }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        ApplicationLifecycleDispatcher.onConfigurationChanged(this, newConfig)
    }
}
