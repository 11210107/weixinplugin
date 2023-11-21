@file:Suppress("UNUSED_EXPRESSION")

package com.magic.xmagichooker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.magic.kernel.callMethod
import com.magic.kernel.callStaticMethod
import com.magic.kernel.findClass
import com.magic.kernel.getBooleanField
import com.magic.kernel.getIntField
import com.magic.kernel.getLongField
import com.magic.kernel.getObjectField
import com.magic.kernel.getStaticObjectField
import com.magic.kernel.utils.ThreadUtil
import com.magic.shared.hookers.interfaces.IActivityHooker
import com.magic.wechat.hookers.interfaces.IFinderLiveHooker
import com.magic.wework.hookers.interfaces.IApplicationHooker

object WechatPlugins : IActivityHooker, IApplicationHooker,IFinderLiveHooker {
    val TAG = WechatPlugins::class.java.simpleName

    var loadLiveInfo = false

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.e(WechatPlugins::class.java.name, "onActivityCreated   class: ${activity.javaClass}")
        if (activity.javaClass.name == WechatClass.FinderLiveVisitorAffinityUI.name) {
            val intent = activity.intent
//            val intent =
//                activity.callMethod(WechatClass.FinderLiveVisitorAffinityUI.Method.getIntent)
            Log.e(WechatPlugins::class.java.name, "FinderLiveVisitorAffinityUI   intent: ${intent}")

            val keyContextId = intent.getStringExtra("key_context_id")
            Log.e(WechatPlugins::class.java.name, "keyContextId:$keyContextId ")


            val keyClickTabContextId = intent.getStringExtra("key_click_tab_context_id")
            Log.e(WechatPlugins::class.java.name, "keyClickTabContextId:$keyClickTabContextId ")

            val keyClickSubTabContextId = intent.getStringExtra("key_click_sub_tab_context_id")
            Log.e(WechatPlugins::class.java.name, "keyClickSubTabContextId:$keyClickSubTabContextId")

            val keyChnlExtra = intent.getStringExtra("key_chnl_extra")
            Log.e(WechatPlugins::class.java.name, "keyChnlExtra:$keyChnlExtra")

        }
        if (activity.javaClass.name == "com.tencent.mm.plugin.finder.feed.ui.FinderProfileUI") {
            val intent = activity.callMethod("getIntent") as Intent
            Log.e(TAG,"onActivityCreated from_teen_mode_setting_page:${intent.callMethod("getBooleanExtra","from_teen_mode_setting_page",false)}")
            Log.e(TAG,"onActivityCreated KEY_DO_NOT_CHECK_ENTER_BIZ_PROFILE:${intent.callMethod("getBooleanExtra","KEY_DO_NOT_CHECK_ENTER_BIZ_PROFILE",false)}")

        }

        Log.e(WechatPlugins::class.java.name, "onActivityCreated *******")
    }

    override fun onCreate(bundle: Bundle) {
        Log.e(WechatPlugins::class.java.name, "onCreate bundle:$bundle -------")
    }

    override fun onResume(uiComponent:Any) {
        Log.e(WechatPlugins::class.java.name, "UIComponent onResume:$uiComponent")
        if (uiComponent.javaClass.name == "com.tencent.mm.plugin.finder.live.viewmodel.component.i") {
            val intent = uiComponent.callMethod("getIntent")
            Log.e(WechatPlugins::class.java.name, "UIComponent intent:$intent")
            val finderLiveBundle = intent?.callMethod("getParcelableExtra", "KEY_PARAMS_CONFIG")
            Log.e(WechatPlugins::class.java.name, "UIComponent finderLiveBundle:$finderLiveBundle")
        }
        if (uiComponent.javaClass.name == "com.tencent.mm.plugin.finder.profile.uic.FinderProfileFeedUIC") {
            //进入视频号主页
            val intent = uiComponent.callMethod("getIntent")
            val liveNoticeId = intent?.callMethod("getStringExtra", "KEY_FINDER_PROFILE_UI_REQUEST_LIST_POSITION")
            Log.e(TAG, "onFragmentResume liveNoticeId:$liveNoticeId")
            val finderProfileFeedLoader = uiComponent.callMethod("getFeedLoader")
            Log.e(TAG, "onFragmentResume finderProfileFeedLoader:$finderProfileFeedLoader")

            val dataList = finderProfileFeedLoader?.callMethod("getDataList")
            Log.e(TAG, "onFragmentResume dataList:$dataList")

            val topicFilterList = finderProfileFeedLoader?.callMethod("getTopicFilterList")


            Log.e(TAG, "onFragmentResume topicFilterList:$topicFilterList")
        }
    }

    override fun getFollowContact(followListPresent: Any, aVar: Any, pVar: Any) {
        Log.e(WechatPlugins::class.java.name, "FollowListPresent:getFollowContact$followListPresent")
        ThreadUtil.submitTask {
            val followList = followListPresent.getObjectField("qAW") as ArrayList<Any>

            Log.e(TAG,"followList:$followList")
            val followListViewCallback = followListPresent.getObjectField("GFr")
            Log.e(TAG,"getFollowContact followListViewCallback:$followListViewCallback")
            val mMActivity = followListViewCallback?.getObjectField("activity") as Activity
            followList.forEach {
                val contact = it.getObjectField("contact")
                Log.e(TAG,"contact:$contact")
                val liveStatus = contact?.getIntField("field_liveStatus")
                Log.e(TAG,"liveStatus:$liveStatus")
                val userName = contact?.callMethod("getUsername") as String
                Log.e(TAG,"getFollowContact userName:$userName")
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




    }

    override fun jumpToLive(baseFinderFeed: Any, i2: Int) {
        Log.e(TAG, "jumpToLive:$baseFinderFeed")
        val finderItem = baseFinderFeed.getObjectField("feedObject")
        Log.e(TAG, "finderItem:$finderItem")
        val finderObject = finderItem?.callMethod("getFeedObject")
        Log.e(TAG, "finderObject:$finderObject")
        val liveInfo = finderObject?.getObjectField("liveInfo")
        Log.e(TAG, "liveInfo:$liveInfo")
        val liveId = liveInfo?.getLongField("liveId")
        Log.e(TAG, "liveId:$liveId")
        val streamUrl = liveInfo?.getObjectField("nom")
        Log.e(TAG, "streamUrl:$streamUrl")
    }

    override fun onFetchDone(finderProfileFeedLoader:Any,iResponse: Any) {
        Log.e(TAG, "onFetchDone:$iResponse")
        if (iResponse.javaClass.name == "com.tencent.mm.plugin.finder.feed.model.FinderProfileFeedLoader\$ProfileResponse") {
            val isRequestAll = iResponse.callMethod("isRequestAll")
            Log.e(TAG, "isRequestAll:$isRequestAll")
            val topicFilterList = iResponse.callMethod("getFilterList")

            Log.e(TAG, "topicFilterList:$topicFilterList")

            val pullType = iResponse.callMethod("getPullType")
            val hasMore = iResponse.callMethod("getHasMore")
            Log.e(TAG, "pullType:$pullType")
            Log.e(TAG, "hasMore:$hasMore")
            if (pullType == 1) {
                val dataBuffer = finderProfileFeedLoader.callMethod("getDataList")
                Log.e(TAG, "onFetchDone dataList:$dataBuffer")
                val finderFeed = dataBuffer?.callMethod("get", 0)
                Log.e(TAG, "onFetchDone finderFeed:$finderFeed")
                val finderItem = finderFeed?.getObjectField("feedObject")
                Log.e(TAG, "onFetchDone finderItem:$finderItem")
                val isLiveFeed = finderItem?.callMethod("isLiveFeed") as Boolean
                if (isLiveFeed) {
                    val finderObject = finderItem?.callMethod("getFinderObject")
                    Log.e(TAG, "finderObject:$finderObject")
                    val liveInfo = finderObject?.getObjectField("liveInfo")
                    Log.e(TAG, "liveInfo:$liveInfo")
                    val liveId = liveInfo?.getLongField("liveId")
                    Log.e(TAG, "liveId:$liveId")
                    val streamUrl = liveInfo?.getObjectField("nom")
                    Log.e(TAG, "streamUrl:$streamUrl")/**/
                }
                loadLiveInfo = true
            }
            val readExist = finderProfileFeedLoader.getBooleanField("readExist")
            val everIn = finderProfileFeedLoader.getBooleanField("everIn")
            val allowPrefetch = finderProfileFeedLoader.getIntField("allowPrefetch")
            val showJustWatch = finderProfileFeedLoader.getIntField("showJustWatch")
            Log.e(TAG, "readExist:$readExist,everIn:$everIn,allowPrefetch:$allowPrefetch,showJustWatch:$showJustWatch")
        }
    }

    override fun getFeedLoader(finderProfileFeedUIC:Any) {
        Log.e(TAG, "FinderProfileFeedUIC onFilterDataChanged ")
        val activity = finderProfileFeedUIC.callMethod("getActivity") as Activity
        Log.e(TAG, "FinderProfileFeedUIC activity$activity ")
        ThreadUtil.runOnMainThread({
            if (loadLiveInfo) {
                exitFinderProfileUI(activity)
            }
        },1500L)

    }

    private fun exitFinderProfileUI(activity: Activity) {
        Log.e(TAG, "FinderProfileFeedUIC exitFinderProfileUI")
        activity.finish()
        loadLiveInfo = false
    }


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
//        Log.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r24:$r24")
//        Log.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r25:$r25")
//        Log.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r26:$r26")
//        Log.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r28:$r28")
//        Log.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r29:$r29")
//        Log.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r30:$r30")
//        Log.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r31:$r31")
//        Log.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r32:$r32")
//        Log.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r33:$r33")
//        Log.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r34:$r34")
//        Log.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r35:$r35")
//        Log.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r36:$r36")
//        Log.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r37:$r37")
//        Log.e(WechatPlugins::class.java.name, "onEnterFinderLiveVisitorUI:r38:$r38")
//    }
//
//    override fun onViewCreated(view: View, bundle: Bundle) {
//        Log.e(WechatPlugins::class.java.name, "onViewCreated bundle:$bundle -------")
//    }

    override fun onFragmentResume(fragment: Any) {
        Log.e(WechatPlugins::class.java.name, "onFragmentResume fragment:${fragment.javaClass}")
        if (fragment.javaClass.name == "com.tencent.mm.plugin.finder.profile.FinderProfileFeedFragment") {


        }
    }

    private fun enterFinderProfileUI(userName: String,mMActivity:Activity){
        Log.e(TAG, "enterFinderProfile userName:$userName")
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
}