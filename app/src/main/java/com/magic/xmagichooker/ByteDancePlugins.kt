package com.magic.xmagichooker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.magic.kernel.callMethod
import com.magic.kernel.findClass
import com.magic.kernel.getObjectField
import com.magic.kernel.utils.ThreadUtil
import com.magic.shared.hookers.interfaces.IActivityHooker
import com.magic.wechat.hookers.interfaces.IScanQRHooker
import com.magic.wework.hookers.interfaces.IApplicationHooker
import com.magic.xmagichooker.model.BaseResult
import com.magic.xmagichooker.model.ScanQRLoginTask
import com.magic.xmagichooker.util.ContextUtil
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
            "6a67cead900200f8" to "352693081166213",
            "6a67cead900200f9" to "352693081166214"
        )
    }

    private var mActivityRef: WeakReference<Activity>? = null

    fun setActivity(activity: Activity) {
        mActivityRef = WeakReference(activity)
    }
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.e(TAG, "onActivityCreated   class: ${activity.javaClass}")
//        if (TextUtils.equals(
//                activity.javaClass.name,
//                "com.ss.android.ugc.aweme.openauthorize.AwemeAuthorizedActivity"
//            ) && !isInit
//        ) {
//            isInit = true
//            val intent = activity.callMethod("getIntent")
//            Log.e(TAG, "onActivityCreated   intent: ${intent}")
//            val data = intent?.callMethod("getData")
//            Log.e(TAG, "onActivityCreated   data: ${data}")
//            val bundle = intent?.callMethod("getExtras")
//            Log.e(TAG, "onActivityCreated   bundle: ${bundle}")
//
//        }

    }
    private @Synchronized fun onInit(activity: Any) {
        Log.e(TAG, "onInit class: ${activity.javaClass.name} isInit:$isInit")
        synchronized(this) {
            if (!isInit && (activity.javaClass.name == "com.ss.android.ugc.aweme.main.MainActivity")) {
                isInit = true
                initDouyin()
            }
        }
    }

    private fun initDouyin() {
        Log.e(TAG, "initDouyin startInterval:")
        IntervalLoginTask.startInterval()
    }

    fun getLoginTask(){
        val androidId =
            Settings.Secure.getString(ContextUtil.weChatApplication.contentResolver, Settings.Secure.ANDROID_ID)
        Log.e(TAG, "getLoginTask   androidId: ${androidId}")
        ThreadUtil.submitTask{
            Thread.sleep(200L)
            val task = NetWorkUtil.get(Define.getLoginTask(), mutableMapOf<String,String>(), deviceId[androidId])
            val response =
                mGson.fromJson<BaseResult<ScanQRLoginTask>>(task.toString(), genericType<BaseResult<ScanQRLoginTask>>())
            if (response.data != null) {
                ThreadUtil.runOnMainThread {
                    Log.e(TAG, "getLoginTask   response: $response")
                    startAwemeAuthActivity(response.data.qrcodeDecodeRaw?:"")
                }
            }
            Log.e(TAG, "getLoginTask   task: ${task}")
            val params = mutableMapOf<String, String>()
            params["brokerCode"] = "100801961"
            params["platform"] = "soft"
            val json = NetWorkUtil.get(Define.getMasterInfo(),params)
            Log.e(TAG, "json:$json")
        }
    }
    override fun onActivityResumed(activity: Activity) {
        Log.e(TAG, "onActivityResumed   class: ${activity.javaClass}")
        onInit(activity)
        setActivity(activity)
    }

    override fun checkParams(activity: Activity, bundle: Bundle) {
        val bundle3 = Bundle()
        bundle3.putString("enter_from", "")
        bundle3.putBoolean("show_switch_account_dialog", false)
        bundle3.putBoolean("not_skip_confirm", true)
        bundle.putBundle("_bytedance_params_extra", bundle3)
        Log.e(TAG, "checkParams   json: ${bundle}")
        val authLevelViewModel = activity.getObjectField("authLevelViewModel")
        Log.e(TAG, "checkParams   authLevelViewModel: ${authLevelViewModel}")
        val LIZIZ = authLevelViewModel?.callMethod("LIZIZ")
        Log.e(TAG, "checkParams1   LIZIZ: ${LIZIZ}")
        val LIZJBundle = LIZIZ?.getObjectField("LIZJ")
        Log.e(TAG, "checkParams1   LIZIZBundle: ${LIZJBundle}")

    }

    override fun onAuthSuccess(activity: Activity, response: Any) {
        Log.e(TAG, "onAuthSuccess   response: ${mGson.toJson(response)}")
    }

    override fun onAuthFailed(activity: Activity, response: Any) {
        Log.e(TAG, "onAuthFailed   response: ${mGson.toJson(response)}")
    }

    override fun sendResponse(activity: Activity, response: Any) {
        Log.e(TAG, "sendResponse   response: ${mGson.toJson(response)}")
    }

    override fun sendResult(activity: Activity, response: Any) {
        Log.e(TAG, "sendResult   response: ${mGson.toJson(response)}")
    }

    private fun startAwemeAuthActivity(qrCodeUrl:String) {
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
        Log.e(TAG, "onActivityCreated   intent2-1: ${intent2}")
        val clientKey = uri.getQueryParameter("client_key")
        Log.e(TAG, "onActivityCreated   clientKey: ${clientKey}")
        val qr_source_aid = uri.getQueryParameter("qr_source_aid")
        Log.e(TAG, "onActivityCreated   qr_source_aid: ${qr_source_aid}")
        val scopes = uri.getQueryParameter("scopes")
        Log.e(TAG, "onActivityCreated   scopes: ${scopes}")
        val token = uri.getQueryParameter("token")
        Log.e(TAG, "onActivityCreated   token: ${token}")
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
        Log.e(TAG, "onActivityCreated intent2: ${intent2}")
        Log.e(TAG, "onActivityCreated bundle2: ${bundle2}")
        mActivityRef?.get()?.startActivity(intent2)
    }
}
inline fun <reified T> genericType() = object : TypeToken<T>() {}.type