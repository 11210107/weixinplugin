package com.magic.xmagichooker;

import android.os.Process;
import android.text.format.DateUtils;

import com.magic.xmagichooker.util.MD5;

public class Define {
    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final long HEART_BEAT_CYCLE =  5 * DateUtils.SECOND_IN_MILLIS;
    public static final long FETCH_CYCLE = DEBUG ? 60 * DateUtils.SECOND_IN_MILLIS : 60 * DateUtils.SECOND_IN_MILLIS;
    public static final long INTERVAL_CYCLE = DEBUG ? 1 * DateUtils.MINUTE_IN_MILLIS : 10 * DateUtils.MINUTE_IN_MILLIS;

    public static final String wechat_app = "wechat";
    public static final String aweme_app = "aweme";
    public static final String TASK_SCAN_QR_CODE = "scanQrcode";
    public static final String TASK_SUBMIT_FINDER_LIST = "uploadAccountList";

    public static final String wechat_login_url = "https://channels.weixin.qq.com/mobile/confirm_login.html?";

    public static String getUploadFilePath() {
        return (DEBUG ? "" : "") + "";
    }
    public static final int UID = Process.myUserHandle().hashCode();
    public static String getMasterInfo(){
        return "https://gateway.liduoxing.cn/sparta/manage/api/1/smart/getMasterInfoByBrockCodeAndPlatform";
    }
    public static String getLoginTask(){
        return "http://mcnspider.aliyun-sh-internal.liduoxing.cn:8000/task/get";
    }
    public static String getFinderList(){
        return "https://channels.weixin.qq.com/cgi-bin/mmfinderassistant-bin/auth/scan-qrcode";
    }
}
