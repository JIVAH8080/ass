package com.nextdns.protector.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nextdns.protector.data.PreferencesManager
import com.nextdns.protector.service.NextDNSVpnService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                val prefsManager = PreferencesManager(context)
                if (prefsManager.isDnsEnabled()) {
                    // Restart VPN service if it was enabled
                    val vpnIntent = Intent(context, NextDNSVpnService::class.java)
                    context.startService(vpnIntent)
                }
            }
        }
    }
}
