package com.magic.kernel.utils

import android.app.Application
import com.magic.kernel.findClass

object ContextUtil {

    val weChatApplication: Application by lazy {
        try {
            val method = "android.app.AppGlobals".findClass().getDeclaredMethod("getInitialApplication")
            method.isAccessible = true
            return@lazy method.invoke(null) as Application
        } catch (e: Exception) {
            val method = "android.app.ActivityThread".findClass().getDeclaredMethod("currentApplication")
            method.isAccessible = true
            return@lazy method.invoke(null) as Application
        }
    }


    val weWorkApplication: Application by lazy {
        try {
            val method = "android.app.AppGlobals".findClass().getDeclaredMethod("getInitialApplication")
            method.isAccessible = true
            return@lazy method.invoke(null) as Application
        } catch (e: Exception) {
            val method = "android.app.ActivityThread".findClass().getDeclaredMethod("currentApplication")
            method.isAccessible = true
            return@lazy method.invoke(null) as Application
        }
    }
}