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

        OkHttpClientProvider.setOkHttpClientFactory(object : OkHttpClientFactory {
            override fun createNewNetworkModuleClient(): OkHttpClient {
                val builder = OkHttpClientProvider.createClientBuilder()
                val dns = DnsOverHttps.Builder().client(builder.build())
                    .url("https://cloudflare-dns.com/dns-query".toHttpUrl())
                    .bootstrapDnsHosts(
                        InetAddress.getByName("1.1.1.1"),
                        InetAddress.getByName("1.0.0.1"),
                        InetAddress.getByName("2606:4700:4700::1111"),
                        InetAddress.getByName("2606:4700:4700::1001")
                    )
                    .build()
                builder.dns(dns)
                return builder.build()
            }
        })

        loadReactNative(this)
        ApplicationLifecycleDispatcher.onApplicationCreate(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        ApplicationLifecycleDispatcher.onConfigurationChanged(this, newConfig)
    }
}
