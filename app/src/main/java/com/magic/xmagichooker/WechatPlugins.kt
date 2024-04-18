@file:Suppress("UNUSED_EXPRESSION")

package com.magic.xmagichooker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import cc.sdkutil.controller.util.LogUtil
import com.google.gson.Gson
import com.magic.kernel.callMethod
import com.magic.kernel.callStaticMethod
import com.magic.kernel.findClass
import com.magic.kernel.getStaticObjectField
import com.magic.kernel.utils.ThreadUtil
import com.magic.shared.hookers.interfaces.IActivityHooker
import com.magic.wechat.hookers.interfaces.IFinderLiveHooker
import com.magic.wework.hookers.interfaces.IApplicationHooker
import com.magic.xmagichooker.model.BaseResult
import com.magic.xmagichooker.model.ScanQRLoginTask
import com.magic.xmagichooker.util.NetWorkUtil
import java.net.URLDecoder


object WechatPlugins : IActivityHooker, IApplicationHooker,IFinderLiveHooker {
    val TAG = WechatPlugins::class.java.simpleName
    val mGson by lazy {
        Gson()
    }
    var loadLiveInfo = false
    var isInit = false
    val finder_account_key = "webview_key_user"
    val mmkv by lazy {
        val MultiProcessMMKV = "com.tencent.mm.sdk.platformtools.MultiProcessMMKV".findClass()
        LogUtil.e(WechatPlugins::class.java.name, "MultiProcessMMKV:$MultiProcessMMKV")
        MultiProcessMMKV.callStaticMethod("getMMKV", "WebViewFontUtil")
    }
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        LogUtil.e(WechatPlugins::class.java.name, "onActivityCreated   class: ${activity.javaClass}")
        if (activity.javaClass.name == WechatClass.FinderLiveVisitorAffinityUI.name) {
            val intent = activity.intent

//            val intent =
//                activity.callMethod(WechatClass.FinderLiveVisitorAffinityUI.Method.getIntent)
            LogUtil.e(WechatPlugins::class.java.name, "FinderLiveVisitorAffinityUI   intent: ${intent}")

            val keyContextId = intent.getStringExtra("key_context_id")
            LogUtil.e(WechatPlugins::class.java.name, "keyContextId:$keyContextId ")


            val keyClickTabContextId = intent.getStringExtra("key_click_tab_context_id")
            LogUtil.e(WechatPlugins::class.java.name, "keyClickTabContextId:$keyClickTabContextId ")

            val keyClickSubTabContextId = intent.getStringExtra("key_click_sub_tab_context_id")
            LogUtil.e(WechatPlugins::class.java.name, "keyClickSubTabContextId:$keyClickSubTabContextId")

            val keyChnlExtra = intent.getStringExtra("key_chnl_extra")
            LogUtil.e(WechatPlugins::class.java.name, "keyChnlExtra:$keyChnlExtra")

        }
        if (activity.javaClass.name == "com.tencent.mm.plugin.profile.ui.ContactInfoUI") {
        }
        if (activity.javaClass.name == "com.tencent.mm.plugin.fts.ui.FTSMainUI") {
            if (!loadLiveInfo) {
                loadLiveInfo = true
        //            com.tencent.mm.bz.c.b(
        //                MMApplicationContext.getContext(),
        //                "webview",
        //                ".ui.tools.WebviewMpUI",
        //                intent
        //            )
        //            val intent = activity.callMethod("getIntent") as Intent
        //            LogUtil.e(TAG,"onActivityCreated from_teen_mode_setting_page:${intent.callMethod("getBooleanExtra","from_teen_mode_setting_page",false)}")
        //            LogUtil.e(TAG,"onActivityCreated KEY_DO_NOT_CHECK_ENTER_BIZ_PROFILE:${intent.callMethod("getBooleanExtra","KEY_DO_NOT_CHECK_ENTER_BIZ_PROFILE",false)}")


            }
        }

        LogUtil.e(WechatPlugins::class.java.name, "onActivityCreated22 *******")

    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        onInit(activity)
    }




    override fun onPageFinished(WebViewUI:Activity,WebView:Any,url: String) {
        LogUtil.e(WechatPlugins::class.java.name, "WebView onPageFinished $url")
        if (url.startsWith(Define.wechat_login_url)) {
            val finder_account = mmkv?.callMethod("decodeString","webview_key_user")
            LogUtil.e(WechatPlugins::class.java.name, "WebView finderAccount $finder_account")
//        val js = "javascript:document.querySelector('.confirm .weui-btn.weui-btn_primary').click()"
//        val js = "javascript:document.querySelectorAll('.multi-confirm .confirm-finder-list .confirm-item .confirm-name .name-content')"
//        val js = "javascript:window.Android.showToast('Hello!')"
            val js = "javascript:document.querySelectorAll('.multi-confirm .confirm-finder-list .confirm-item .confirm-name .name-content').forEach(item => {\n" +
                    "  if(item.innerText === '$finder_account'){\n" +
                    "    item.click()\n" +
                    "  }\n" +
                    "})"
            LogUtil.e(WechatPlugins::class.java.name, "WebView js $js")
            ThreadUtil.runOnMainThread({
                LogUtil.e(WechatPlugins::class.java.name, "WebView before evaluateJavascript")
                /*val valueCallback2 = object :ValueCallback<String>{
                    override fun onReceiveValue(args: String?) {
                        LogUtil.e(WechatPlugins::class.java.name, "WebView ValueCallback2 args: ${args}")
                    }

                }
                WebView.callMethod("addJavascriptInterface",WechatJavascriptInterface(),"Android")*/
                WebView.callMethod("evaluateJavascript",js,null)
                LogUtil.e(WechatPlugins::class.java.name, "WebView after evaluateJavascript")
                ThreadUtil.runOnMainThread({
                    LogUtil.e(WechatPlugins::class.java.name, "WebViewUI finish")
                    WebViewUI.callMethod("finish")
                },3000L)
            },3000L)

        }


    }


    private fun openWebViewUI(url:String,account:String) {
        mmkv?.callMethod("putString", finder_account_key, account)
        mmkv?.callMethod("apply")
        val intent = Intent()
//        intent.putExtra("rawUrl", "https://channels.weixin.qq.com/mobile/confirm_login.html?token=${token}")
        intent.putExtra("rawUrl", url)
        val cClass = "com.tencent.mm.bz.c".findClass()
        LogUtil.e(WechatPlugins::class.java.name, "cClass:$cClass")
        val MMApplicationContextClass =
            "com.tencent.mm.sdk.platformtools.MMApplicationContext".findClass()
        val context = MMApplicationContextClass.callStaticMethod("getContext") as Context
        cClass.callStaticMethod("b",context,"webview",".ui.tools.WebviewMpUI",intent)
    }
    private @Synchronized fun onInit(activity: Any) {
        synchronized(this) {
            if (!isInit && (activity.javaClass.name == "com.tencent.mm.ui.LauncherUI")) {
                isInit = true
                init()
            }
        }
    }

//    override fun onCreate(bundle: Bundle) {
//        LogUtil.e(WechatPlugins::class.java.name, "onCreate bundle:$bundle -------")
//    }
//
//    override fun onResume(uiComponent:Any) {
//        LogUtil.e(WechatPlugins::class.java.name, "UIComponent onResume:$uiComponent")
//        if (uiComponent.javaClass.name == "com.tencent.mm.plugin.finder.live.viewmodel.component.i") {
//            val intent = uiComponent.callMethod("getIntent")
//            LogUtil.e(WechatPlugins::class.java.name, "UIComponent intent:$intent")
//            val finderLiveBundle = intent?.callMethod("getParcelableExtra", "KEY_PARAMS_CONFIG")
//            LogUtil.e(WechatPlugins::class.java.name, "UIComponent finderLiveBundle:$finderLiveBundle")
//        }
//        if (uiComponent.javaClass.name == "com.tencent.mm.plugin.finder.profile.uic.FinderProfileFeedUIC") {
//            //进入视频号主页
//            val intent = uiComponent.callMethod("getIntent")
//            val liveNoticeId = intent?.callMethod("getStringExtra", "KEY_FINDER_PROFILE_UI_REQUEST_LIST_POSITION")
//            LogUtil.e(TAG, "onFragmentResume liveNoticeId:$liveNoticeId")
//            val finderProfileFeedLoader = uiComponent.callMethod("getFeedLoader")
//            LogUtil.e(TAG, "onFragmentResume finderProfileFeedLoader:$finderProfileFeedLoader")
//
//            val dataList = finderProfileFeedLoader?.callMethod("getDataList")
//            LogUtil.e(TAG, "onFragmentResume dataList:$dataList")
//
//            val topicFilterList = finderProfileFeedLoader?.callMethod("getTopicFilterList")
//
//
//            LogUtil.e(TAG, "onFragmentResume topicFilterList:$topicFilterList")
//        }
//    }


    /*override fun getFollowContact(followListPresent: Any, aVar: Any, pVar: Any) {
        LogUtil.e(WechatPlugins::class.java.name, "FollowListPresent:getFollowContact$followListPresent")
        ThreadUtil.submitTask {
            val followList = followListPresent.getObjectField("qAW") as ArrayList<Any>

            LogUtil.e(TAG,"followList:$followList")
            val followListViewCallback = followListPresent.getObjectField("GFr")
            LogUtil.e(TAG,"getFollowContact followListViewCallback:$followListViewCallback")
            val mMActivity = followListViewCallback?.getObjectField("activity") as Activity
            followList.forEach {
                val contact = it.getObjectField("contact")
                LogUtil.e(TAG,"contact:$contact")
                val liveStatus = contact?.getIntField("field_liveStatus")
                LogUtil.e(TAG,"liveStatus:$liveStatus")
                val userName = contact?.callMethod("getUsername") as String
                LogUtil.e(TAG,"getFollowContact userName:$userName")
                if (liveStatus == 1) {
                    mMActivity?.let {
                        ThreadUtil.runOnMainThread({
                            enterFinderProfileUI(userName,it)
                        },1000L)
                    }
                }
                Thread.sleep(3000)
            }

        }




    }*/

//    override fun jumpToLive(baseFinderFeed: Any, i2: Int) {
//        LogUtil.e(TAG, "jumpToLive:$baseFinderFeed")
//        val finderItem = baseFinderFeed.getObjectField("feedObject")
//        LogUtil.e(TAG, "finderItem:$finderItem")
//        val finderObject = finderItem?.callMethod("getFeedObject")
//        LogUtil.e(TAG, "finderObject:$finderObject")
//        val liveInfo = finderObject?.getObjectField("liveInfo")
//        LogUtil.e(TAG, "liveInfo:$liveInfo")
//        val liveId = liveInfo?.getLongField("liveId")
//        LogUtil.e(TAG, "liveId:$liveId")
//        val streamUrl = liveInfo?.getObjectField("nom")
//        LogUtil.e(TAG, "streamUrl:$streamUrl")
//    }

//    override fun onFetchDone(finderProfileFeedLoader:Any,iResponse: Any) {
//        LogUtil.e(TAG, "onFetchDone:$iResponse")
//        if (iResponse.javaClass.name == "com.tencent.mm.plugin.finder.feed.model.FinderProfileFeedLoader\$ProfileResponse") {
//            val isRequestAll = iResponse.callMethod("isRequestAll")
//            LogUtil.e(TAG, "isRequestAll:$isRequestAll")
//            val topicFilterList = iResponse.callMethod("getFilterList")
//
//            LogUtil.e(TAG, "topicFilterList:$topicFilterList")
//
//            val pullType = iResponse.callMethod("getPullType")
//            val hasMore = iResponse.callMethod("getHasMore")
//            LogUtil.e(TAG, "pullType:$pullType")
//            LogUtil.e(TAG, "hasMore:$hasMore")
//            if (pullType == 1) {
//                val dataBuffer = finderProfileFeedLoader.callMethod("getDataList")
//                LogUtil.e(TAG, "onFetchDone dataList:$dataBuffer")
//                val finderFeed = dataBuffer?.callMethod("get", 0)
//                LogUtil.e(TAG, "onFetchDone finderFeed:$finderFeed")
//                val finderItem = finderFeed?.getObjectField("feedObject")
//                LogUtil.e(TAG, "onFetchDone finderItem:$finderItem")
//                val isLiveFeed = finderItem?.callMethod("isLiveFeed") as Boolean
//                if (isLiveFeed) {
//                    val finderObject = finderItem?.callMethod("getFinderObject")
//                    LogUtil.e(TAG, "finderObject:$finderObject")
//                    val liveInfo = finderObject?.getObjectField("liveInfo")
//                    LogUtil.e(TAG, "liveInfo:$liveInfo")
//                    val liveId = liveInfo?.getLongField("liveId")
//                    LogUtil.e(TAG, "liveId:$liveId")
//                    val streamUrl = liveInfo?.getObjectField("nom")
//                    LogUtil.e(TAG, "streamUrl:$streamUrl")/**/
//                }
//                loadLiveInfo = true
//            }
//            val readExist = finderProfileFeedLoader.getBooleanField("readExist")
//            val everIn = finderProfileFeedLoader.getBooleanField("everIn")
//            val allowPrefetch = finderProfileFeedLoader.getIntField("allowPrefetch")
//            val showJustWatch = finderProfileFeedLoader.getIntField("showJustWatch")
//            LogUtil.e(TAG, "readExist:$readExist,everIn:$everIn,allowPrefetch:$allowPrefetch,showJustWatch:$showJustWatch")
//        }
//    }

    /*override fun getFeedLoader(finderProfileFeedUIC:Any) {
        LogUtil.e(TAG, "FinderProfileFeedUIC onFilterDataChanged ")
        val activity = finderProfileFeedUIC.callMethod("getActivity") as Activity
        LogUtil.e(TAG, "FinderProfileFeedUIC activity$activity ")
        ThreadUtil.runOnMainThread({
            if (loadLiveInfo) {
                exitFinderProfileUI(activity)
            }
        },1500L)

    }*/

//    private fun exitFinderProfileUI(activity: Activity) {
//        LogUtil.e(TAG, "FinderProfileFeedUIC exitFinderProfileUI")
//        activity.finish()
//        loadLiveInfo = false
//    }


//    override fun onEnterFinderLiveVisitorUI(
//        r24: Intent,
//        r25: Context,
//        r26: Long,
//        r28: Long,
//        r29: String,
//        r30: String,
//        r31: String,
//        r32: String,
//        r33: String,
//        r34: String,
//        r35: Int,
//        r36: String,
//        r37: Int,
//        r38: Boolean
//    ) {
//        LogUtil.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r24:$r24")
//        LogUtil.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r25:$r25")
//        LogUtil.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r26:$r26")
//        LogUtil.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r28:$r28")
//        LogUtil.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r29:$r29")
//        LogUtil.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r30:$r30")
//        LogUtil.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r31:$r31")
//        LogUtil.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r32:$r32")
//        LogUtil.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r33:$r33")
//        LogUtil.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r34:$r34")
//        LogUtil.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r35:$r35")
//        LogUtil.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r36:$r36")
//        LogUtil.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r37:$r37")
//        LogUtil.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r38:$r38")
//    }
//
//    override fun onViewCreated(view: View, bundle: Bundle) {
//        LogUtil.e(WechatPlugins::class.java.name, "onViewCreated bundle:$bundle -------")
//    }

//    override fun onFragmentResume(fragment: Any) {
//        LogUtil.e(WechatPlugins::class.java.name, "onFragmentResume fragment:${fragment.javaClass}")
//        if (fragment.javaClass.name == "com.tencent.mm.plugin.finder.profile.FinderProfileFeedFragment") {
//
//
//        }
//    }

    private fun enterFinderProfileUI(userName: String,mMActivity:Activity){
        LogUtil.e(TAG, "enterFinderProfile userName:$userName")
        val intent = Intent()
        intent.putExtra("finder_username", userName)
        intent.putExtra("from_teen_mode_setting_page", false)
        intent.putExtra("KEY_FROM_TIMELINE", true)
        intent.putExtra(
            "KEY_DO_NOT_CHECK_ENTER_BIZ_PROFILE",
            true
        )
        val finderReporterUICCompanion = "com.tencent.mm.plugin.finder.viewmodel.component.ax\$a".findClass()
        finderReporterUICCompanion.callStaticMethod("a",mMActivity, intent, 0L, 1, false, 64)
        val activityRouterClazz = "com.tencent.mm.plugin.finder.utils.a".findClass()
        val activityRouter = activityRouterClazz.getStaticObjectField("HRh")
        activityRouter?.callMethod("enterFinderProfileUI", mMActivity, intent)
    }

    private fun init() {
        LogUtil.e(TAG, "init AlarmManager")
        IntervalLoginTask.startInterval(true)
    }



    fun getLoginTask(){
        val params = mutableMapOf<String, String?>()
        params["deviceId"] = getWechatAlias()
        ThreadUtil.submitTask{
            Thread.sleep(200L)
            val json = NetWorkUtil.get(Define.getLoginTask(),params)
            LogUtil.e(WechatPlugins::class.java.name, "json:$json")
            val response =
                mGson.fromJson<BaseResult<ScanQRLoginTask>>(
                    json.toString(),
                    genericType<BaseResult<ScanQRLoginTask>>()
                )
            val scanQRLoginTask = response.data
            if (scanQRLoginTask != null) {
                ThreadUtil.runOnMainThread {
                    LogUtil.e(TAG, "getLoginTask response: $response")
                    LogUtil.e(TAG, "getLoginTask extra: ${scanQRLoginTask?.extra?.selectorName}")
                    val account = URLDecoder.decode(scanQRLoginTask?.extra?.selectorName ?: "","UTF-8")
                    openWebViewUI(scanQRLoginTask.qrcodeDecodeRaw?: "",account)
                }
            }
        }
    }

    private fun getWechatAlias():String?{
        try {
            val mConfigStorageLogic = "com.tencent.mm.model.z".findClass()
            LogUtil.e(WechatPlugins::class.java.name, "mConfigStorageLogic:$mConfigStorageLogic")
            val curAlias = mConfigStorageLogic.callStaticMethod("buA") as String?
            LogUtil.e(WechatPlugins::class.java.name, "curAlias:$curAlias")
            val curUsername = mConfigStorageLogic.callStaticMethod("buz")
            LogUtil.e(WechatPlugins::class.java.name, "curUsername:$curUsername")
            return if (TextUtils.isEmpty(curAlias)) {
                curUsername as String
            } else {
                curAlias
            }
        } catch (e: Error) {
            e.printStackTrace()
        }
        return null
    }

}