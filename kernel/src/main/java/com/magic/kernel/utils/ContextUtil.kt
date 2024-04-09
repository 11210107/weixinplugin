package com.magic.kernel.utils

import android.app.Application
import com.magic.kernel.findClass
import java.lang.reflect.Method

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

    private val sApplication: Application? = null

    fun get(): Application? {
        return sApplication ?: getApplication()
    }

    private fun getApplication(): Application? {
        var application: Application? = null
        var method: Method
        try {
            method =
                Class.forName("android.app.AppGlobals").getDeclaredMethod("getInitialApplication")
            method.isAccessible = true
            application = method.invoke(null) as Application
        } catch (e: java.lang.Exception) {
            try {
                method = Class.forName("android.app.ActivityThread")
                    .getDeclaredMethod("currentApplication")
                method.isAccessible = true
                application = method.invoke(null) as Application
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }
        return application
    }

}