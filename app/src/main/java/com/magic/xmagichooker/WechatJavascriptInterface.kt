package com.magic.xmagichooker

import android.webkit.JavascriptInterface
import android.widget.Toast
import cc.sdkutil.controller.util.LogUtil
import com.magic.kernel.utils.ContextUtil

class WechatJavascriptInterface{
        @JavascriptInterface
        fun jsCallJavaWithArgs(args:String){
            LogUtil.e(WechatPlugins::class.java.name, "WebView jsCallJavaWithArgs args[0]: ${args}")
        }

        @JavascriptInterface
        fun showToast(message: String?) {
            LogUtil.e(WechatPlugins::class.java.name, "WebView showToast message: $message")
            Toast.makeText(ContextUtil.get(), message, Toast.LENGTH_SHORT).show()
        }
    }