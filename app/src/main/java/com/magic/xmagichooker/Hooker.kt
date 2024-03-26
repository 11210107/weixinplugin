package com.magic.xmagichooker

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.text.TextUtils
import android.util.Log
import com.magic.kernel.MagicHooker
import com.magic.kernel.helper.TryHelper.tryVerbosely
import com.magic.shared.apis.SharedEngine
import com.magic.wechat.apis.WcEngine
import com.magic.wework.apis.WwEngine
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage


class Hooker : IXposedHookLoadPackage, IXposedHookZygoteInit {

    private val TARGET_PACKAGE = "com.magic.xmagichooker"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        Log.e(Hooker::class.java.name, "handleLoadPackage   ${lpparam.processName}")
        tryVerbosely {
            when (lpparam.packageName) {
                TARGET_PACKAGE ->
                    hookAttachBaseContext(lpparam.classLoader) {
                        hookLoadHooker(lpparam.classLoader)
                    }
                else -> if (MagicHooker.isImportantWechatProcess(lpparam)) {
                    hookAttachBaseContext(lpparam.classLoader) {
                        hookTencent(lpparam, it)
                    }
                }
            }
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        Log.e(Hooker::class.java.name, "initZygote   ${startupParam?.modulePath}   ${startupParam?.startsSystemServer}")
    }

    private fun hookAttachBaseContext(classLoader: ClassLoader, callback: (Context) -> Unit) {
        XposedHelpers.findAndHookMethod(
            "android.content.ContextWrapper",
            classLoader,
            "attachBaseContext",
            Context::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
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
        Log.e(Hooker::class.java.name, "lpparam.packageName:${lpparam.packageName}")
        Log.e(Hooker::class.java.name, "lpparam.processName:${lpparam.processName}")
        when (lpparam.packageName) {
            "com.tencent.wework" -> {
                MagicHooker.startup(
                    lpparam = lpparam,
                    plugins = listOf(Plugins),
                    centers = WwEngine.hookerCenters + SharedEngine.hookerCenters
                )
            }
            "com.tencent.mm" -> {
                Log.e(Hooker::class.java.name, "开始启动个人微信插件")
                val processName = getProcessName(MagicHooker.getSystemContext())
                Log.e(Hooker::class.java.name, "processName:$processName")
                if (TextUtils.equals("com.tencent.mm", processName)) {

                }
                MagicHooker.startup(
                    lpparam = lpparam,
                    plugins = listOf(WechatPlugins),
                    centers = WcEngine.hookerCenters + SharedEngine.hookerCenters
                )
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