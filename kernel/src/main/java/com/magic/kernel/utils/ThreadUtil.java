package com.magic.kernel.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cc.sdkutil.controller.util.LogUtil;

@SuppressLint("LogUse")
public class ThreadUtil {
    public static Handler hGc = null;
    public static final ThreadPoolExecutor hGd = new ThreadPoolExecutor(5, 50, 10, TimeUnit.SECONDS, new LinkedBlockingQueue(50), new ThreadPoolExecutor.DiscardOldestPolicy()) {
        /* access modifiers changed from: protected */
        public <T> RunnableFuture<T> newTaskFor(Runnable runnable, T t) {
            return newTaskFor(new a(runnable, (String) null, t));
        }

        /* access modifiers changed from: protected */
        public <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
            if (!(callable instanceof a)) {
                callable = new a<>(callable, (String) null);
            }
            return new FutureTask(callable);
        }
    };
    /* access modifiers changed from: private */
    public static final String processName = ("@" + getAppNameByPID(Process.myPid()) + "-pool_thread-");

    private static String getAppNameByPID(int pid) {
        ActivityManager manager = (ActivityManager) ContextUtil.INSTANCE.get().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo runningAppProcess : manager.getRunningAppProcesses()) {
            if (runningAppProcess.pid == pid) {
                return runningAppProcess.processName;
            }
        }
        return "";
    }
    public static Handler sHandler = new Handler(Looper.getMainLooper());

    static {
        hGd.setThreadFactory(new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, ThreadUtil.processName + this.threadNumber.getAndIncrement());
                if (thread.isDaemon()) {
                    thread.setDaemon(false);
                }
                if (thread.getPriority() != 5) {
                    thread.setPriority(5);
                }
                return thread;
            }
        });
    }

    public static void u(Runnable runnable) {
        e(runnable, 0);
    }

    public static void e(final Runnable runnable, final long j) {
        Handler handler = hGc;
        if (handler == null) {
            new HandlerThread("backgroupd-looper") {
                /* access modifiers changed from: protected */
                public void onLooperPrepared() {
                    super.onLooperPrepared();
                    try {
                        ThreadUtil.hGc = new Handler(getLooper());
                        if (j > 0) {
                            ThreadUtil.hGc.postDelayed(runnable, j);
                        } else {
                            ThreadUtil.hGc.post(runnable);
                        }
                    } catch (Exception e) {
                        LogUtil.INSTANCE.e("ThreadUtils", "postInBackgroundLooper err:"+ e);
                    }
                }
            }.start();
        } else if (j > 0) {
            handler.postDelayed(runnable, j);
        } else {
            handler.post(runnable);
        }
    }

    public static void v(Runnable runnable) {
        sHandler.post(runnable);
    }

    public static void runOnMainThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            sHandler.post(runnable);
        }
    }

    public static void runOnMainThread(Runnable runnable, long j) {
        if (runnable != null) {
            sHandler.postDelayed(runnable, j);
        }
    }

    public static void w(Runnable runnable) {
        if (runnable != null) {
            sHandler.removeCallbacks(runnable);
        }
    }

    public static void x(Runnable runnable) {
        sHandler.postAtTime(runnable, 0);
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public static void y(Runnable runnable) {
        sHandler.removeCallbacks(runnable);
    }

    public static void submitTask(Runnable runnable) {
        c(runnable, (String) null);
    }

    public static void c(Runnable runnable, String str) {
        hGd.submit(new a(runnable, str, null));
    }

    public static <T> Future<T> e(Callable<T> callable) {
        return hGd.submit(callable);
    }

    /* renamed from: fmw$a */
    /* compiled from: ThreadUtils */
    static class a<V> implements Callable<V> {
        private static long count;
        Runnable hGe;
        Callable<V> hGf;
        String mName;
        V mResult;

        public a(Runnable runnable, String str, V v) {
            this.hGe = runnable;
            this.mName = str;
            if (TextUtils.isEmpty(this.mName)) {
                this.mName = runnable.getClass().getName();
            }
            count++;
        }

        public a(Callable<V> callable, String str) {
            this.hGf = callable;
            this.mName = str;
            if (TextUtils.isEmpty(this.mName)) {
                this.mName = callable.getClass().getName();
            }
            count++;
        }


        public V call() {
            String name = Thread.currentThread().getName();
            try {
                name = this.mName + name.substring(name.lastIndexOf("@"));
                Thread.currentThread().setName(name);
            } catch (Throwable unused) {
            }
            Runnable runnable = this.hGe;
            if (runnable != null) {
                try {
                    runnable.run();
                } catch (Throwable th) {
                    LogUtil.INSTANCE.d("ThreadUtils", "runOnBackground"+name+ th);
                }
            }
            Callable<V> callable = this.hGf;
            if (callable != null) {
                try {
                    this.mResult = callable.call();
                } catch (Throwable th2) {
                    LogUtil.INSTANCE.d("ThreadUtils", "runOnBackground" + name + th2);
                }
            }
            return this.mResult;
        }
    }
}