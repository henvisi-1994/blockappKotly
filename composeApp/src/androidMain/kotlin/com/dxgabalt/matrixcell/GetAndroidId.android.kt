package com.dxgabalt.matrixcell

import android.content.Context
import android.provider.Settings

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class GetAndroidId {
    actual fun getDeviceIdentifier(): String {
          val context = AndroidContext.appContext
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "Unknown ID"
    }
}


