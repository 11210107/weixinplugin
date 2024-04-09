package com.magic.xmagichooker.util;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;



public class TransferUtil {


    public static void openWeChat(Context context){
        try {
//            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
//            context.startActivity(intent);

            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
            ToastUtil.getInstance().showToast("检查到您手机没有安装微信，请安装后使用该功能");
        }

    }


    public static void openWeWork(Context context){
        try {
//            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
//            context.startActivity(intent);

            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.wework","com.tencent.wework.launch.LaunchSplashActivity");

            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
            ToastUtil.getInstance().showToast("检查到您手机没有安装企业微信，请安装后使用该功能");
        }

    }

    public static void openAwemeAPP(Context context){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.ss.android.ugc.aweme","com.ss.android.ugc.aweme.splash.SplashActivity");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
        }

    }



}
