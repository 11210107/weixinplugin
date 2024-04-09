package com.magic.xmagichooker

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import cc.sdkutil.controller.util.LogUtil
import com.magic.kernel.utils.ContextUtil
import com.magic.xmagichooker.util.RootUtil
import com.magic.xmagichooker.util.TransferUtil

class ForegroundCoreService : Service() {
    val TAG = ForegroundCoreService::class.java.simpleName
    override fun onBind(p0: Intent?): IBinder? = null
    companion object{
        val ACTION_RUN_INTERVAL_TASK = "ACTION_RUN_INTERVAL_TASK" + Define.UID
    }

    private val mForegroundNF: ForegroundNF by lazy { ForegroundNF(this) }

    override fun onCreate() {
        super.onCreate()
        mForegroundNF.startForegroundNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtil.d(TAG, "onStartCommand intent: $intent, flags: $flags")
        if (null == intent) {
            //服务被系统kill掉之后重启进来的
            return START_NOT_STICKY
        }else{
            if (TextUtils.equals(ACTION_RUN_INTERVAL_TASK,intent.action)) {
                LogUtil.d(TAG, "onStartCommand openWeChat")
                val rootUtil = RootUtil()
                rootUtil.topActivity
                if (TextUtils.equals(BuildConfig.TARGET_APP, Define.wechat_app)) {
                    if (!rootUtil.result.last().contains("com.tencent.mm")) {
                        //当前页面不是微信
                        TransferUtil.openWeChat(ContextUtil.get())
                    }
                }
                if (TextUtils.equals(BuildConfig.TARGET_APP, Define.aweme_app)) {
                    if (!rootUtil.result.last().contains("com.ss.android.ugc.aweme")) {
                        //当前页面不是抖音
                        TransferUtil.openAwemeAPP(ContextUtil.get())
                    }
                }

            }
        }
//        mForegroundNF.startForegroundNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mForegroundNF.stopForegroundNotification()
        super.onDestroy()
    }
}