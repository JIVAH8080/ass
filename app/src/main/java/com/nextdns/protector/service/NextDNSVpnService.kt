package com.nextdns.protector.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import com.nextdns.protector.MainActivity
import com.nextdns.protector.data.NextDNSConfig
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

class NextDNSVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var isRunning = false
    private var vpnThread: Thread? = null

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "nextdns_vpn_channel"
        private const val MTU = 1500
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            startVpn()
        }
        return START_STICKY
    }

    private fun startVpn() {
        try {
            val builder = Builder()
                .setSession("NextDNS")
                .setMtu(MTU)
                .addAddress("10.0.0.2", 24)
                .addAddress("fd00:1:fd00:1:fd00:1:fd00:1", 64)

            // Add NextDNS servers
            NextDNSConfig.IPV4_DNS_SERVERS.forEach { dns ->
                builder.addDnsServer(dns)
            }

            NextDNSConfig.IPV6_ADDRESSES.forEach { dns ->
                builder.addDnsServer(dns)
            }

            // Route all traffic through VPN
            builder.addRoute("0.0.0.0", 0)
            builder.addRoute("::", 0)

            // Set up the VPN interface
            vpnInterface = builder.establish()

            if (vpnInterface != null) {
                isRunning = true
                startForeground(NOTIFICATION_ID, createNotification())

                // Start packet forwarding thread
                vpnThread = Thread {
                    runVpn()
                }
                vpnThread?.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }
    }

    private fun runVpn() {
        try {
            val inputStream = FileInputStream(vpnInterface?.fileDescriptor)
            val outputStream = FileOutputStream(vpnInterface?.fileDescriptor)
            val buffer = ByteBuffer.allocate(MTU)

            while (isRunning) {
                // Read packets from VPN interface
                val length = inputStream.read(buffer.array())
                if (length > 0) {
                    // Forward packets (simplified - in production, implement proper packet handling)
                    buffer.limit(length)
                    // Process and forward packet
                    buffer.clear()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
    }

    private fun stopVpn() {
        isRunning = false
        vpnThread?.interrupt()
        vpnInterface?.close()
        vpnInterface = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "NextDNS VPN Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "NextDNS VPN is running"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }

        return builder
            .setContentTitle("NextDNS Protection Active")
            .setContentText("Your DNS queries are protected")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
