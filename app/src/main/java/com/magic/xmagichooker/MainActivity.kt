package com.magic.xmagichooker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cc.sdkutil.controller.util.LogUtil
import com.magic.kernel.utils.CmdUtil
import com.magic.kernel.utils.ContextUtil
import com.magic.kernel.utils.ThreadUtil
import com.magic.xmagichooker.util.RootUtil
import com.magic.xmagichooker.util.TransferUtil
import kotlinx.android.synthetic.main.activity_main.btn_reboot
import kotlinx.android.synthetic.main.activity_main.btn_start_aweme
import kotlinx.android.synthetic.main.activity_main.btn_start_interval_task
import kotlinx.android.synthetic.main.activity_main.btn_start_service
import kotlinx.android.synthetic.main.activity_main.btn_start_web
import kotlinx.android.synthetic.main.activity_main.btn_start_wechat
import kotlinx.android.synthetic.main.activity_main.sample_text
import java.net.URLDecoder


class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val isRoot = CmdUtil.isRoot
        LogUtil.d("Hooker","isRoot=$isRoot")
        if (isRoot) {
            sample_text.text = "hooked15 = true"
        }
        val qrUrl =
            "aweme://authorizedy?certification_scope=\\u0026client_key=aw7tduvjdk1a0x3r\\u0026comment_id=\\u0026enter_from=\\u0026not_skip_confirm=true\\u0026openid=\\u0026optional_scope_check=\\u0026optional_scope_uncheck=\\u0026qr_source_aid=544162\\u0026scopes=mobile%2Cuser_info%2Cvideo.create%2Cvideo.data\\u0026source=pc_auth\\u0026token=e2caec6c18175d54f87a53fc0d243af1_lq\\u0026user_ticket="
        sample_text.text = URLDecoder.decode(qrUrl)
        btn_reboot.setOnClickListener {
            RootUtil().reboot()
        }
        btn_start_service.setOnClickListener {
            startService(Intent(this, ForegroundCoreService::class.java))
        }
        btn_start_web.setOnClickListener {
            val uri = Uri.parse("https://work.weixin.qq.com/ca/cawcde686d532a9191")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            ThreadUtil.submitTask {
                runWithTry(5,3000L) {
                    val rootUtil = RootUtil()
                    rootUtil.topActivity
                    LogUtil.d(TAG, rootUtil.result.last())
                    if (!rootUtil.result.last().contains("plugin.profile.ui.ContactInfoUI")) throw IllegalStateException("not wechat add friend page")
                }

            }


        }
        val application = ContextUtil.get()
        btn_start_aweme.setOnClickListener {
            TransferUtil.openAwemeAPP(application)
        }
        btn_start_wechat.setOnClickListener {
            TransferUtil.openWeChat(application)
        }
        btn_start_interval_task.setOnClickListener {
            val intent = Intent(application, ForegroundCoreService::class.java)
            intent.setAction(ForegroundCoreService.ACTION_RUN_INTERVAL_TASK)
            val pendingIntent = PendingIntent.getService(application, 111, intent, 0)
            val alarmManager = application?.getSystemService(ALARM_SERVICE) as AlarmManager?
            LogUtil.d(TAG,"start ForegroundCoreService")
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent)
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + Define.INTERVAL_CYCLE,
                    Define.INTERVAL_CYCLE,
                    pendingIntent
                )
            } else {
                LogUtil.e(TAG, "alarmManager is null !!!")
            }
        }
    }



    override fun onResume() {
        super.onResume()
//        if (checkHook()) {
//            val path = MagicHooker.getApplicationApkPath("com.magic.xmagichooker")
//            sample_text.text = "hooked = true  \n  \n $path"
//        }
    }

    fun checkHook(): Boolean {
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
