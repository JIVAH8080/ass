package com.nextdns.protector.service

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import com.nextdns.protector.data.NextDNSConfig

class DnsConfigManager(private val context: Context) {

    fun configureDns(method: String = "auto"): Boolean {
        return when {
            method == "private_dns" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                configurePrivateDns()
            }
            method == "vpn" || method == "auto" -> {
                configureVpnDns()
            }
            else -> false
        }
    }

    private fun configurePrivateDns(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // Guide user to set Private DNS
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                
                // Note: Automatic configuration requires system permissions
                // User must manually set: e628d2.dns.nextdns.io
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun configureVpnDns(): Boolean {
        return try {
            val intent = Intent(context, NextDNSVpnService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun stopDns() {
        try {
            val intent = Intent(context, NextDNSVpnService::class.java)
            context.stopService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentDnsServers(): List<String> {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork
            val linkProperties = connectivityManager.getLinkProperties(activeNetwork)
            linkProperties?.dnsServers?.map { it.hostAddress ?: "" } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun isNextDnsActive(): Boolean {
        val currentDns = getCurrentDnsServers()
        return currentDns.any { dns ->
            NextDNSConfig.ALL_DNS_SERVERS.any { nextDns ->
                dns.contains(nextDns, ignoreCase = true)
            }
        }
    }
}
