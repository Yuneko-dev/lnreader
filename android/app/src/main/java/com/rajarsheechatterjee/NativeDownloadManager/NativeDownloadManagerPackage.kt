package com.rajarsheechatterjee.NativeDownloadManager

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider

class NativeDownloadManagerPackage : BaseReactPackage() {
    override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? =
        if (name == "NativeDownloadManager") NativeDownloadManager(reactContext) else null

    override fun getReactModuleInfoProvider() = ReactModuleInfoProvider {
        mapOf(
            "NativeDownloadManager" to ReactModuleInfo(
                "NativeDownloadManager",
                "NativeDownloadManager",
                false, 
                false, 
                true, 
                false, 
                true
            )
        )
    }
}
