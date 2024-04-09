package com.magic.xmagichooker

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.text.TextUtils
import android.util.Log
import cc.sdkutil.controller.util.LogUtil
import com.magic.kernel.MagicHooker
import com.magic.kernel.helper.TryHelper.tryVerbosely
import com.magic.shared.apis.SharedEngine
import com.magic.wechat.apis.ByteDanceEngine
import com.magic.wechat.apis.WcEngine
import com.magic.wework.apis.WwEngine
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File


class Hooker : IXposedHookLoadPackage, IXposedHookZygoteInit {

    private val TARGET_PACKAGE = "com.magic.xmagichooker"
    private val BYTE_DANCE_PACKAGE = "com.ss.android.ugc.aweme"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        LogUtil.e(Hooker::class.java.name, "handleLoadPackage   ${lpparam.processName}")
        tryVerbosely {
            when (lpparam.packageName) {
                TARGET_PACKAGE ->
                    hookAttachBaseContext(lpparam.classLoader) {
                        hookLoadHooker(lpparam.classLoader)
                    }
                BYTE_DANCE_PACKAGE ->
//                    hookAttachBaseContext(lpparam.classLoader) {
//
//                    }
                    hookByteDance(lpparam)
                else -> if (MagicHooker.isImportantWechatProcess(lpparam)) {
                    hookAttachBaseContext(lpparam.classLoader) {
                        hookTencent(lpparam, it)
                    }
                }
            }
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        LogUtil.e(Hooker::class.java.name, "initZygote   ${startupParam?.modulePath}   ${startupParam?.startsSystemServer}")
    }

    private fun hookAttachBaseContext(classLoader: ClassLoader, callback: (Context) -> Unit) {
        XposedHelpers.findAndHookMethod(
            "android.content.ContextWrapper",
            classLoader,
            "attachBaseContext",
            Context::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    LogUtil.e(Hooker::class.java.name, "hookAttachBaseContext callback")
                    callback(param?.thisObject as? Application ?: return)
                }
            })
    }

    private fun hookLoadHooker(classLoader: ClassLoader) {
        XposedHelpers.findAndHookMethod(
            "$TARGET_PACKAGE.MainActivity", classLoader,
            "checkHook", object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any = true
            })
    }

    private fun hookTencent(lpparam: XC_LoadPackage.LoadPackageParam, context: Context) {
        LogUtil.e(Hooker::class.java.name, "lpparam.packageName:${lpparam.packageName}")
        LogUtil.e(Hooker::class.java.name, "lpparam.processName:${lpparam.processName}")
        when (lpparam.packageName) {
            "com.tencent.wework" -> {
                MagicHooker.startup(
                    lpparam = lpparam,
                    plugins = listOf(Plugins),
                    centers = WwEngine.hookerCenters + SharedEngine.hookerCenters
                )
            }
            "com.tencent.mm" -> {
                LogUtil.e(Hooker::class.java.name, "开始启动个人微信插件")
                val processName = getProcessName(MagicHooker.getSystemContext())
                LogUtil.e(Hooker::class.java.name, "processName:$processName")
                if (TextUtils.equals("com.tencent.mm", processName)) {
                    //主进程hook
                }
                MagicHooker.startup(
                    lpparam = lpparam,
                    plugins = listOf(WechatPlugins),
                    centers = WcEngine.hookerCenters + SharedEngine.hookerCenters
                )
            }
        }
    }

    private fun hookByteDance(lpparam: XC_LoadPackage.LoadPackageParam){
        LogUtil.e(Hooker::class.java.name, "lpparam.packageName:${lpparam.packageName}")
        LogUtil.e(Hooker::class.java.name, "lpparam.processName:${lpparam.processName}")
        when (lpparam.processName) {
            "com.ss.android.ugc.aweme" ->{
                LogUtil.e(Hooker::class.java.name, "开始启动抖音插件")
                val processName = getProcessName(MagicHooker.getSystemContext())
                LogUtil.e(Hooker::class.java.name, "mainProcessName:$processName")
                if (TextUtils.equals("com.ss.android.ugc.aweme", processName)) {
                    //主进程hook
                    MagicHooker.startup(
                        lpparam = lpparam,
                        plugins = listOf(ByteDancePlugins),
                        centers = ByteDanceEngine.hookerCenters + SharedEngine.hookerCenters
                    )
                }

            }
        }
    }

    fun getProcessName(cxt: Context): String? {
        val pid = Process.myPid()
        val am = cxt.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (procInfo in runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName
            }
        }
        return null
    }
}