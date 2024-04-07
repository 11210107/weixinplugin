package com.magic.xmagichooker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.magic.kernel.MagicHooker
import com.magic.kernel.media.audio.AudioHelper
import com.magic.kernel.okhttp.HttpClients
import com.magic.kernel.okhttp.IHttpConfigs
import com.magic.kernel.utils.CmdUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.net.URLDecoder

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val isRoot = CmdUtil.isRoot
        Log.d("Hooker","isRoot=$isRoot")
        if (isRoot) {
            sample_text.text = "hooked15 = true"
        }
        val qrUrl =
            "aweme://authorizedy?certification_scope=\\u0026client_key=aw7tduvjdk1a0x3r\\u0026comment_id=\\u0026enter_from=\\u0026not_skip_confirm=true\\u0026openid=\\u0026optional_scope_check=\\u0026optional_scope_uncheck=\\u0026qr_source_aid=544162\\u0026scopes=mobile%2Cuser_info%2Cvideo.create%2Cvideo.data\\u0026source=pc_auth\\u0026token=e2caec6c18175d54f87a53fc0d243af1_lq\\u0026user_ticket="
        sample_text.text = URLDecoder.decode(qrUrl)
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
