package com.magic.xmagichooker;

import android.text.format.DateUtils;

import com.magic.xmagichooker.util.MD5;

public class Define {
    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final long HEART_BEAT_CICLE =  5 * DateUtils.SECOND_IN_MILLIS;
    public static final long FETCH_CICLE = BuildConfig.DEBUG ? 60 * DateUtils.SECOND_IN_MILLIS : 60 * DateUtils.SECOND_IN_MILLIS;
    public static String getUploadFilePath() {
        return (DEBUG ? "" : "") + "";
    }

    public static String getMasterInfo(){
        return "https://gateway.liduoxing.cn/sparta/manage/api/1/smart/getMasterInfoByBrockCodeAndPlatform";
    }
}
