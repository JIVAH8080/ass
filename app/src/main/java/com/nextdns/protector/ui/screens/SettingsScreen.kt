package com.nextdns.protector.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nextdns.protector.data.NextDNSConfig
import com.nextdns.protector.data.PreferencesManager
import com.nextdns.protector.service.DnsConfigManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefsManager = remember { PreferencesManager(context) }
    val dnsManager = remember { DnsConfigManager(context) }
    
    val dnsMethod by prefsManager.dnsMethodFlow.collectAsState(initial = "auto")
    var selectedMethod by remember { mutableStateOf(dnsMethod) }
    var showMethodDialog by remember { mutableStateOf(false) }
    
    val currentDnsServers = remember { dnsManager.getCurrentDnsServers() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        // DNS Method Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                    Text(
                        text = "DNS Configuration Method",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Divider()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Current Method",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = when (dnsMethod) {
                                "auto" -> "Automatic (VPN)"
                                "vpn" -> "VPN Service"
                                "private_dns" -> "Private DNS (Android 9+)"
                                else -> "Unknown"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(onClick = { showMethodDialog = true }) {
                        Text("Change")
                    }
                }
            }
        }

        // Current DNS Servers Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Current DNS Servers",
                    style = MaterialTheme.typography.titleMedium
                )
                Divider()
                
                if (currentDnsServers.isEmpty()) {
                    Text(
                        text = "No DNS servers detected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    currentDnsServers.forEach { dns ->
                        Text(
                            text = dns,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // NextDNS Configuration Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "NextDNS Configuration",
                    style = MaterialTheme.typography.titleMedium
                )
                Divider()
                
                InfoRow("Profile ID", NextDNSConfig.PROFILE_ID)
                InfoRow("DNS-over-TLS", NextDNSConfig.DNS_OVER_TLS)
                InfoRow("Primary IPv4", NextDNSConfig.IPV4_DNS_SERVERS[0])
                InfoRow("Secondary IPv4", NextDNSConfig.IPV4_DNS_SERVERS[1])
                InfoRow("Primary IPv6", NextDNSConfig.IPV6_ADDRESSES[0])
                InfoRow("Secondary IPv6", NextDNSConfig.IPV6_ADDRESSES[1])
            }
        }

        // App Info Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Text(
                        text = "App Information",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Divider()
                
                InfoRow("Version", "1.0.0")
                InfoRow("Package", "com.nextdns.protector")
                InfoRow("Build", "Release")
            }
        }

        // About Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "About NextDNS Protector",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "This app configures your device to use NextDNS for enhanced security, privacy, and parental controls. Device administrator privileges are used to protect the app from uninstallation.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (showMethodDialog) {
        AlertDialog(
            onDismissRequest = { showMethodDialog = false },
            title = { Text("Select DNS Method") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Choose how to configure DNS:")
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMethod == "auto",
                            onClick = { selectedMethod = "auto" }
                        )
                        Text("Automatic (Recommended)")
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMethod == "vpn",
                            onClick = { selectedMethod = "vpn" }
                        )
                        Text("VPN Service")
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMethod == "private_dns",
                            onClick = { selectedMethod = "private_dns" }
                        )
                        Text("Private DNS (Android 9+)")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            prefsManager.setDnsMethod(selectedMethod)
                        }
                        showMethodDialog = false
                    }
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMethodDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
