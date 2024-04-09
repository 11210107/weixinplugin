package com.magic.xmagichooker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import cc.sdkutil.controller.util.LogUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.magic.kernel.callMethod
import com.magic.kernel.callStaticMethod
import com.magic.kernel.findClass
import com.magic.kernel.getObjectField
import com.magic.kernel.utils.ThreadUtil
import com.magic.shared.hookers.interfaces.IActivityHooker
import com.magic.wechat.hookers.interfaces.IScanQRHooker
import com.magic.wework.hookers.interfaces.IApplicationHooker
import com.magic.xmagichooker.model.BaseResult
import com.magic.xmagichooker.model.ScanQRLoginTask
import com.magic.xmagichooker.util.NetWorkUtil
import java.lang.ref.WeakReference


object ByteDancePlugins : IActivityHooker, IApplicationHooker, IScanQRHooker {
    val TAG = ByteDancePlugins::class.java.simpleName

    var isInit = false
    var isOpen = false
    val mGson by lazy {
        Gson()
    }
    val deviceId by lazy {
        mutableMapOf(
            "6a67cead900200f8" to "352693081166214",
            "6a67cead900200f9" to "352693081166213"
        )
    }

    private var mActivityRef: WeakReference<Activity>? = null

    fun setActivity(activity: Activity) {
        mActivityRef = WeakReference(activity)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        LogUtil.e(TAG, "onActivityCreated   class: ${activity.javaClass}")
        if (TextUtils.equals(
                activity.javaClass.name,
                "com.ss.android.ugc.aweme.profile.ui.HeaderDetailActivity"
            )
        ) {
//            isInit = true
//            val intent = activity.callMethod("getIntent")
//            LogUtil.e(TAG, "onActivityCreated   intent: ${intent}")
//            val data = intent?.callMethod("getData")
//            LogUtil.e(TAG, "onActivityCreated   data: ${data}")
//            val bundle = intent?.callMethod("getExtras")
//            LogUtil.e(TAG, "onActivityCreated   bundle: ${bundle}")
            getUniqueId()
        }

    }

    private fun getUniqueId():String? {
        val AccountProxyService = "com.ss.android.ugc.aweme.account.AccountProxyService".findClass()
        val mIAccountService = AccountProxyService.callStaticMethod("get")
        val mIAccountUserService = mIAccountService?.callMethod("userService")

        val mUser = mIAccountUserService?.callMethod("getCurUser")
//        LogUtil.e(TAG, "mUser: $mUser")
        /*LogUtil.e(TAG, "uniqueId: ${mUser?.getObjectField("uniqueId")}")
        LogUtil.e(TAG, "shortId: ${mUser?.getObjectField("shortId")}")
        val mCurSecUserId = mIAccountUserService?.callMethod("getCurSecUserId")
        LogUtil.e(TAG, "mCurSecUserId: $mCurSecUserId")
        val mCurUserId = mIAccountUserService?.callMethod("getCurUserId")
        LogUtil.e(TAG, "mCurUserId: $mCurUserId")*/
        val uniqueId = mUser?.getObjectField("uniqueId") as String?
        return if (TextUtils.isEmpty(uniqueId)) {
            mUser?.getObjectField("shortId") as String?
        }else{
            uniqueId
        }

    }

    private @Synchronized
    fun onInit(activity: Any) {
        LogUtil.e(TAG, "onInit class: ${activity.javaClass.name} isInit:$isInit")
        synchronized(this) {
            if (!isInit && (activity.javaClass.name == "com.ss.android.ugc.aweme.main.MainActivity")) {
                isInit = true
                initDouyin()
            }
        }
    }

    private fun initDouyin() {
        LogUtil.e(TAG, "initDouyin startInterval:")
        IntervalLoginTask.startInterval(false)
    }

    fun getLoginTask() {
        LogUtil.e(TAG, "getLoginTask:")
        val byteDanceParams = mutableMapOf<String, String?>()
        byteDanceParams["deviceId"] = getUniqueId()
        ThreadUtil.submitTask {
            Thread.sleep(200L)
            val task = NetWorkUtil.get(
                Define.getLoginTask(),
                byteDanceParams
            )
            val response =
                mGson.fromJson<BaseResult<ScanQRLoginTask>>(
                    task.toString(),
                    genericType<BaseResult<ScanQRLoginTask>>()
                )
            if (response.data != null) {
                ThreadUtil.runOnMainThread {
                    LogUtil.e(TAG, "getLoginTask   response: $response")
                    startAwemeAuthActivity(response.data.qrcodeDecodeRaw ?: "")
                }
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {
        LogUtil.e(TAG, "onActivityResumed   class: ${activity.javaClass}")
        onInit(activity)
        setActivity(activity)
    }

    override fun checkParams(activity: Activity, bundle: Bundle) {
        val bundle3 = Bundle()
        bundle3.putString("enter_from", "")
        bundle3.putBoolean("show_switch_account_dialog", false)
        bundle3.putBoolean("not_skip_confirm", true)
        bundle.putBundle("_bytedance_params_extra", bundle3)
        LogUtil.e(TAG, "checkParams   json: ${bundle}")
        val authLevelViewModel = activity.getObjectField("authLevelViewModel")
        LogUtil.e(TAG, "checkParams   authLevelViewModel: ${authLevelViewModel}")
        val LIZIZ = authLevelViewModel?.callMethod("LIZIZ")
        LogUtil.e(TAG, "checkParams1   LIZIZ: ${LIZIZ}")
        val LIZJBundle = LIZIZ?.getObjectField("LIZJ")
        LogUtil.e(TAG, "checkParams1   LIZIZBundle: ${LIZJBundle}")

    }

    override fun onAuthSuccess(activity: Activity, response: Any) {
        LogUtil.e(TAG, "onAuthSuccess   response: ${mGson.toJson(response)}")
    }

    override fun onAuthFailed(activity: Activity, response: Any) {
        LogUtil.e(TAG, "onAuthFailed   response: ${mGson.toJson(response)}")
    }

    override fun sendResponse(activity: Activity, response: Any) {
        LogUtil.e(TAG, "sendResponse   response: ${mGson.toJson(response)}")
    }

    override fun sendResult(activity: Activity, response: Any) {
        LogUtil.e(TAG, "sendResult   response: ${mGson.toJson(response)}")
    }

    private fun startAwemeAuthActivity(qrCodeUrl: String) {
        val intent2 = Intent(
            mActivityRef?.get(),
            "com.ss.android.ugc.aweme.openauthorize.AwemeAuthorizedActivity".findClass()
        )
        intent2.setPackage("com.ss.android.ugc.aweme")
        val uri = Uri.parse(
            qrCodeUrl.replace(
                "\\u0026",
                "&"
            )
        )
        intent2.data = uri
        LogUtil.e(TAG, "onActivityCreated   intent2-1: ${intent2}")
        val clientKey = uri.getQueryParameter("client_key")
        LogUtil.e(TAG, "onActivityCreated   clientKey: ${clientKey}")
        val qr_source_aid = uri.getQueryParameter("qr_source_aid")
        LogUtil.e(TAG, "onActivityCreated   qr_source_aid: ${qr_source_aid}")
        val scopes = uri.getQueryParameter("scopes")
        LogUtil.e(TAG, "onActivityCreated   scopes: ${scopes}")
        val token = uri.getQueryParameter("token")
        LogUtil.e(TAG, "onActivityCreated   token: ${token}")
        val bundle2 = Bundle()
        bundle2.putString("key_qrcode_token", token)
        bundle2.putInt("wap_requested_orientation", -1)
        bundle2.putInt("qr_source_aid", qr_source_aid?.toIntOrNull() ?: 0)
        bundle2.putString("scopes", scopes)
        bundle2.putString("source", "pc_auth")
        bundle2.putString(
            "key_calling_context",
            "com.ss.android.ugc.aweme.app.host.AwemeHostApplication"
        )
        bundle2.putString("token", token)
        bundle2.putInt("_bytedance_params_type", 1)
        bundle2.putString("client_key", clientKey)
        bundle2.putBoolean("not_skip_confirm", true)
        bundle2.putString("_bytedance_params_client_key", clientKey)
        val bundle3 = Bundle()
        bundle3.putString("enter_from", "")
        bundle3.putBoolean("show_switch_account_dialog", false)
        bundle3.putBoolean("not_skip_confirm", true)
        bundle2.putBundle("_bytedance_params_extra", Bundle.EMPTY)
        bundle2.putString(
            "_aweme_params_verify_scope",
            "{\"verify_openid\":\"\",\"verify_scope\":\"\"}"
        )
        intent2.putExtras(bundle2)
        LogUtil.e(TAG, "onActivityCreated intent2: ${intent2}")
        LogUtil.e(TAG, "onActivityCreated bundle2: ${bundle2}")
        mActivityRef?.get()?.startActivity(intent2)
    }
}

inline fun <reified T> genericType() = object : TypeToken<T>() {}.type