package com.jangnal.gaja.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jangnal.gaja.MainActivity
import com.jangnal.gaja.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MarketAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val database = AppDatabase.getDatabase(context)
        val marketDao = database.marketDao()

        CoroutineScope(Dispatchers.IO).launch {
            val favorites = marketDao.getFavoriteMarketsList()
            val todayMillis = System.currentTimeMillis()
            
            // Filter non-permanent markets that open today
            val openMarkets = favorites.filter { !it.isPermanent() && it.isOpenOn(todayMillis) }
            
            if (openMarkets.isNotEmpty()) {
                val marketNames = openMarkets.joinToString(", ") { it.marketName }
                sendNotification(context, marketNames)
            }
        }
    }

    private fun sendNotification(context: Context, marketNames: String) {
        val channelId = "market_notifications"
        val notificationId = 2002

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "장날 알림",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "단골시장 개장일 아침 알림 제공"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🔔 단골시장 장날 알림")
            .setContentText("오늘 단골시장 [ $marketNames ]이 개장했습니다! 장보러 가볼까요? 🏪")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
