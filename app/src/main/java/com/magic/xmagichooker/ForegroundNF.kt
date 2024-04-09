package com.magic.xmagichooker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.core.app.NotificationCompat

class ForegroundNF(private val service: ForegroundCoreService) : ContextWrapper(service) {
    companion object {
        private const val START_ID = 101
        private const val CHANNEL_ID = "app_foreground_service"
        private const val CHANNEL_NAME = "前台保活服务"
    }

    private var mNotificationManager: NotificationManager? = null

    private var mCompatBuilder: NotificationCompat.Builder? = null

    private val compatBuilder: NotificationCompat.Builder?
        get() {
            if (mCompatBuilder == null) {
                val notificationIntent = Intent(this, MainActivity::class.java)
                notificationIntent.action = Intent.ACTION_MAIN
                notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                notificationIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                // 动作意图
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    (Math.random() * 10 + 10).toInt(),
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                val notificationBuilder:NotificationCompat.Builder = NotificationCompat.Builder(this,
                    CHANNEL_ID)
                // 标题
                notificationBuilder.setContentTitle(getString(R.string.notification_content))
                // 通知内容
                notificationBuilder.setContentTitle(getString(R.string.notification_sub_content))
                // 状态栏显示的小图标
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                // 点击后跳转的Intent
                notificationBuilder.setContentIntent(pendingIntent)
                mCompatBuilder = notificationBuilder
            }
            return mCompatBuilder
        }
    init {
        createNotificationChannel()
    }

    /**
     * 创建通知渠道
     */
    private fun createNotificationChannel() {
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 针对8.0+的系统
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.setShowBadge(false)
            mNotificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * 显示通知
     */
    fun startForegroundNotification(){
        service.startForeground(START_ID,compatBuilder?.build())
    }

    /**
     * 停止并隐藏通知
     */
    fun stopForegroundNotification(){
        mNotificationManager?.cancelAll()
        service.stopForeground(true)
    }
}