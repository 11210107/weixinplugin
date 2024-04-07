package com.magic.xmagichooker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.magic.xmagichooker.util.ContextUtil
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.timerTask

object IntervalLoginTask {
    val TAG = IntervalLoginTask::class.java.simpleName
    var heartBeatTimer: Timer? = null
    var heartBeatTask: TimerTask? = null
    const val ACTION_RUN_FETCH_TASK = "ACTION_RUN_FETCH_TASK"
    private val intentFilter = IntentFilter(ACTION_RUN_FETCH_TASK)

    fun startInterval(){
        ContextUtil.weChatApplication.registerReceiver(receiver, intentFilter)
        val alarmManager =
            ContextUtil.weChatApplication.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(ACTION_RUN_FETCH_TASK)
        val pendingIntent =
            PendingIntent.getBroadcast(ContextUtil.weChatApplication, 111, intent, 0)
        alarmManager.cancel(pendingIntent)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + Define.HEART_BEAT_CICLE,
            Define.FETCH_CICLE,
            pendingIntent
        )
        ContextUtil.weChatApplication.sendBroadcast(intent)
    }

    private var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e(TAG, "onReceive")
            val action = intent.action
            if (ACTION_RUN_FETCH_TASK.equals(action)) {
                Log.e(TAG, "start intervalHeartTask")
                intervalHeartTask()
            }
        }
    }
    private fun intervalHeartTask() {
        cancelTask()
        heartBeatTimer = Timer()
        heartBeatTask = timerTask {
            Log.e(TAG, "timerTask heartBeat")
            ByteDancePlugins.getLoginTask()
        }
        heartBeatTimer?.schedule(heartBeatTask, 2000L, Define.HEART_BEAT_CICLE)
    }
    private fun cancelTask() {
        heartBeatTimer?.cancel()
        heartBeatTask?.cancel()
    }
}