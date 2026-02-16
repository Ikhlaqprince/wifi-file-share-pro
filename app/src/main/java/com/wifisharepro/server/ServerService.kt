package com.wifisharepro.server

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ServerService : Service() {

    private var server: FileServer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val port = intent?.getIntExtra("port", 8080) ?: 8080

        server = FileServer(port, this)
        server?.start()

        startForeground(1, createNotification(port))

        return START_STICKY
    }

    override fun onDestroy() {
        server?.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(port: Int): Notification {

        val channelId = "wifi_share_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "WiFi Share Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("WiFi File Share Running")
            .setContentText("Port: $port")
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .build()
    }
}
