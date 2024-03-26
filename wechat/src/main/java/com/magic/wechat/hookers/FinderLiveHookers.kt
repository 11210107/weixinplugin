package com.magic.wechat.hookers

import android.util.Log
import com.magic.kernel.core.Clazz
import com.magic.kernel.core.Hooker
import com.magic.kernel.core.HookerCenter
import com.magic.kernel.findClass
import com.magic.wechat.hookers.interfaces.IFinderLiveHooker

object FinderLiveHookers:HookerCenter() {
    override val interfaces: List<Class<*>>
        get() = listOf(IFinderLiveHooker::class.java)


    override fun provideEventHooker(event: String): Hooker? {
        Log.e("WechatHooker", "FinderLiveHooker provideEventHooker $event")
        return when (event) {
//            "onEnterFinderLiveVisitorUI" -> iMethodNotifyHooker(
//                clazz = "com.tencent.mm.plugin.finder.live.PluginFinderLive".findClass(),
//                method = "enterFinderLiveVisitorUI",
//                iClazz = IFinderLiveHooker::class.java,
//                iMethodBefore = event,
//                needObject = false,
//                parameterTypes = *arrayOf(
//                    Clazz.Intent, Clazz.Context,Clazz.Long,Clazz.Long,Clazz.String,Clazz.String,Clazz.String,Clazz.String,Clazz.String,Clazz.String,Clazz.Int,Clazz.String,Clazz.Int,Clazz.Boolean
//                )
//            )
//            "onCreate" -> iMethodNotifyHooker(
//                clazz = "com.tencent.mm.ui.component.UIComponent".findClass(),
//                method = "onCreate",
//                iClazz = IFinderLiveHooker::class.java,
//                iMethodBefore = event,
//                needObject = false,
//                parameterTypes = *arrayOf(
//                    Clazz.Bundle
//                )
//            )
//            "onResume" -> iMethodNotifyHooker(
//                clazz = "com.tencent.mm.ui.component.UIComponent".findClass(),
//                method = "onResume",
//                iClazz = IFinderLiveHooker::class.java,
//                iMethodBefore = event,
//                needObject = true
//            )
//            "onViewCreated" -> iMethodNotifyHooker(
//                clazz = "com.tencent.mm.plugin.finder.live.fragment.FinderLiveVisitorFragment".findClass(),
//                method = event,
//                iClazz = IFinderLiveHooker::class.java,
//                iMethodBefore = "onViewCreated",
//                needObject = false,
//                parameterTypes = *arrayOf(Clazz.View,Clazz.Bundle)
//            )
//            "onFragmentResume" ->
//                iMethodNotifyHooker(
//                    clazz = "com.tencent.mm.ui.component.UIComponentFragment".findClass(),
//                    method = "onResume",
//                    iClazz = IFinderLiveHooker::class.java,
//                    iMethodAfter = "onFragmentResume",
//                    needObject = true
//                )
//
//            "getFollowContact" ->
//                iMethodNotifyHooker(
//                    clazz = "com.tencent.mm.plugin.finder.presenter.contract.FinderFollowListContract\$FollowListPresent".findClass(),
//                    method = "a",
//                    iClazz = IFinderLiveHooker::class.java,
//                    iMethodAfter = "getFollowContact",
//                    needObject = false,
//                    parameterTypes = *arrayOf(
//                        "com.tencent.mm.plugin.finder.presenter.contract.FinderFollowListContract\$FollowListPresent".findClass(),
//                        "kotlin.g.b.ah\$a".findClass(),
//                        "com.tencent.mm.ao.p".findClass()
//                    )
//                )
//
//            "jumpToLive" ->
//                iMethodNotifyHooker(
//                    clazz = "com.tencent.mm.plugin.finder.profile.uic.FinderProfileFeedUIC".findClass(),
//                    method = "jumpToLive",
//                    iClazz = IFinderLiveHooker::class.java,
//                    iMethodAfter = "jumpToLive",
//                    needObject = false,
//                    parameterTypes = *arrayOf(
//                        "com.tencent.mm.plugin.finder.model.BaseFinderFeed".findClass(),
//                        Clazz.Int
//                    )
//                )
//
//            "onFetchDone" ->
//                iMethodNotifyHooker(
//                    clazz = "com.tencent.mm.plugin.finder.feed.model.FinderProfileFeedLoader".findClass(),
//                    method = "onFetchDone",
//                    iClazz = IFinderLiveHooker::class.java,
//                    iMethodAfter = "onFetchDone",
//                    needObject = true,
//                    parameterTypes = *arrayOf(
//                        "com.tencent.mm.plugin.finder.feed.model.internal.IResponse".findClass()
//                    )
//                )
//
//            "getFeedLoader" ->
//                iMethodNotifyHooker(
//                    clazz = "com.tencent.mm.plugin.finder.profile.uic.FinderProfileFeedUIC".findClass(),
//                    method = "getFeedLoader",
//                    iClazz = IFinderLiveHooker::class.java,
//                    iMethodAfter = "getFeedLoader",
//                    needObject = true
//                )
//            "getContext" ->
//                iMethodNotifyHooker(
//                    clazz = "com.tencent.mm.sdk.platformtools.MMApplicationContext".findClass(),
//                    method = "getContext",
//                    iClazz = IFinderLiveHooker::class.java,
//                    iMethodAfter = "getContext",
//                    needObject = false
//
//                )
//            "onWindowFocusChanged" ->
//                iMethodNotifyHooker(
//                    clazz = "com.tencent.mm.plugin.webview.d.j".findClass(),
//                    method = "bs",
//                    iClazz = IFinderLiveHooker::class.java,
//                    iMethodBefore = "onWindowFocusChanged",
//                    needObject = false,
//                    parameterTypes = *arrayOf(Clazz.Boolean,Clazz.Boolean)
//                )
            "b" ->
                iMethodNotifyHooker(
                    clazz = "com.tencent.mm.plugin.webview.ui.tools.WebViewUI".findClass(),
                    method = "b",
                    iClazz = IFinderLiveHooker::class.java,
                    iMethodBefore = "b",
                    needObject = false,
                    parameterTypes = *arrayOf("com.tencent.xweb.WebView".findClass(),Clazz.String)
                )
            else -> throw IllegalArgumentException("Unknown event: $event")
        }
    }

}