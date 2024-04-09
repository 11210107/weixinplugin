package com.magic.xmagichooker.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

/**
 * Created by weijia.jin on 16/4/27.
 */
public class ToastUtil {
    private static final String TAG = "ToastUtil";
    private static volatile ToastUtil mToastUtil = null;
    private static Context context;
    private Toast mToast = null;
    private Toast lToast = null;
    private final static int DEFAULT_TEXT_SIZE = 18;

    public static void init(Context targetContext) {
        context = targetContext;
    }


    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String msg) {
        ToastUtil.getInstance().showToast((Object) msg);
    }

    public static void showToast(Context context, @StringRes int strId) {
        showToast(context, context.getString(strId));
    }

    /**
     * 获取实例
     *
     * @return
     */
    public static ToastUtil getInstance() {
        if (mToastUtil == null) {
            synchronized (ToastUtil.class) {
                if (mToastUtil == null) {
                    mToastUtil = new ToastUtil();
                }
            }
        }
        return mToastUtil;
    }

    protected Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 显示Toast，多次调用此函数时，Toast显示的时间不会累计，并且显示内容为最后一次调用时传入的内容
     * 持续时间默认为short
     *
     * @param tips 要显示的内容
     *             {@link Toast#LENGTH_LONG}
     */
    public void showToast(final Object tips) {
        showToast(tips, Toast.LENGTH_SHORT);
    }

    public void showlongToast(final Object tips) {
        showToast(tips, Toast.LENGTH_LONG);
    }

    /**
     * 显示Toast，多次调用此函数时，Toast显示的时间不会累计，并且显示内容为最后一次调用时传入的内容
     *
     * @param tips     要显示的内容
     * @param duration 持续时间，参见{@link Toast#LENGTH_SHORT}和
     *                 {@link Toast#LENGTH_LONG}
     */
    public void showToast(final Object tips, final int duration) {
        final String content = String.valueOf(tips);
        if (android.text.TextUtils.isEmpty(content) || "null".equals(content)) {
            return;
        }

        try {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mToast == null) {
                        mToast = Toast.makeText(context, content, duration);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                    } else {
                        //mToast.cancel();
                        //mToast.setILoginView(mToast.getView());
                        mToast.setText(content);
                        mToast.setDuration(duration);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showLargeToast(final Object tips, final int durantion) {
        final String content = String.valueOf(tips);
        if (android.text.TextUtils.isEmpty(content) || "null".equals(content)) {
            return;
        }
        try {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (lToast == null) {
                        lToast = Toast.makeText(context, content, durantion);
                        LinearLayout layout = (LinearLayout) lToast.getView();
                        TextView v = layout.findViewById(android.R.id.message);
                        v.setTextSize(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE);
                    } else {
                        lToast.setText(content);
                        lToast.setDuration(durantion);
                    }
                    lToast.show();
                }
            });
        } catch (Exception e) {

        }
    }
}
