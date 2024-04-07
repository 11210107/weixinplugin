package com.magic.wechat.hookers.interfaces

import android.app.Activity
import android.content.Context
import android.os.Bundle
import org.json.JSONObject

interface IScanQRHooker {
    fun checkParams(activity: Activity,bundle: Bundle)
    fun onAuthSuccess(activity: Activity,response: Any)
    fun onAuthFailed(activity: Activity,response: Any)
    fun sendResponse(activity: Activity,response: Any)
    fun sendResult(activity: Activity,response: Any)
}