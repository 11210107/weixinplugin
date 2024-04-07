package com.magic.wechat.hookers

import android.util.Log
import com.magic.kernel.core.Clazz
import com.magic.kernel.core.Hooker
import com.magic.kernel.core.HookerCenter
import com.magic.kernel.findClass
import com.magic.wechat.hookers.interfaces.IScanQRHooker

object ScanQRHooker : HookerCenter() {
    override val interfaces: List<Class<*>>
        get() = listOf(IScanQRHooker::class.java)

    override fun provideEventHooker(event: String): Hooker? {
        Log.e("DYHooker", "IScanQRHooker provideEventHooker $event")
        return when (event) {
            "checkParams" ->
                iMethodNotifyHooker(
                    clazz = "com.ss.android.ugc.aweme.openauthorize.AwemeAuthorizedActivity".findClass(),
                    method = "checkParams",
                    iClazz = IScanQRHooker::class.java,
                    iMethodBefore = "checkParams",
                    needObject = true,
                    parameterTypes = *arrayOf(Clazz.Bundle)
                )
            "onAuthSuccess" ->
                iMethodNotifyHooker(
                    clazz = "com.ss.android.ugc.aweme.openauthorize.AwemeAuthorizedActivity".findClass(),
                    method = "onAuthSuccess",
                    iClazz = IScanQRHooker::class.java,
                    iMethodBefore = "onAuthSuccess",
                    needObject = true,
                    parameterTypes = *arrayOf("com.bytedance.sdk.account.common.model.SendAuth\$Response".findClass())
                )
            "onAuthFailed" ->
                iMethodNotifyHooker(
                    clazz = "com.ss.android.ugc.aweme.openauthorize.AwemeAuthorizedActivity".findClass(),
                    method = "onAuthFailed",
                    iClazz = IScanQRHooker::class.java,
                    iMethodBefore = "onAuthFailed",
                    needObject = true,
                    parameterTypes = *arrayOf("com.bytedance.sdk.account.common.model.SendAuth\$Response".findClass())
                )
            "sendResponse" ->
                iMethodNotifyHooker(
                    clazz = "com.ss.android.ugc.aweme.openauthorize.AwemeAuthorizedActivity".findClass(),
                    method = "sendResponse",
                    iClazz = IScanQRHooker::class.java,
                    iMethodBefore = "sendResponse",
                    needObject = true,
                    parameterTypes = *arrayOf("com.bytedance.sdk.account.common.model.SendAuth\$Response".findClass())
                )
            "sendResult" ->
                iMethodNotifyHooker(
                    clazz = "com.ss.android.ugc.aweme.openauthorize.AwemeAuthorizedActivity".findClass(),
                    method = "sendResult",
                    iClazz = IScanQRHooker::class.java,
                    iMethodBefore = "sendResult",
                    needObject = true,
                    parameterTypes = *arrayOf("com.bytedance.sdk.account.common.model.SendAuth\$Response".findClass())
                )
            else -> throw IllegalArgumentException("Unknown event: $event")
        }
    }
}