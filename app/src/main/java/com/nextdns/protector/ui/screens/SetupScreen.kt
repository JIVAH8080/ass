package com.nextdns.protector.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nextdns.protector.admin.DeviceAdminManager
import com.nextdns.protector.data.NextDNSConfig
import com.nextdns.protector.data.PreferencesManager
import com.nextdns.protector.service.DnsConfigManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefsManager = remember { PreferencesManager(context) }
    val adminManager = remember { DeviceAdminManager(context) }
    val dnsManager = remember { DnsConfigManager(context) }

    var isAdminActive by remember { mutableStateOf(adminManager.isAdminActive()) }
    var isDnsEnabled by remember { mutableStateOf(false) }
    var isNextDnsActive by remember { mutableStateOf(false) }
    var showAdminDialog by remember { mutableStateOf(false) }
    var showVpnDialog by remember { mutableStateOf(false) }

    val dnsEnabledState by prefsManager.dnsEnabledFlow.collectAsState(initial = false)

    LaunchedEffect(Unit) {
        isDnsEnabled = dnsEnabledState
        isNextDnsActive = dnsManager.isNextDnsActive()
    }

    val adminLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isAdminActive = adminManager.isAdminActive()
        scope.launch {
            prefsManager.setDeviceAdminEnabled(isAdminActive)
        }
    }

    val vpnLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            scope.launch {
                dnsManager.configureDns("vpn")
                prefsManager.setDnsEnabled(true)
                isDnsEnabled = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "NextDNS Setup",
            style = MaterialTheme.typography.headlineMedium
        )

        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isAdminActive && isDnsEnabled) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isAdminActive && isDnsEnabled) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.Warning
                    },
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = if (isAdminActive && isDnsEnabled) {
                            "Protection Active"
                        } else {
                            "Protection Inactive"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (isAdminActive && isDnsEnabled) {
                            "Your device is protected"
                        } else {
                            "Complete setup to enable protection"
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Profile Info Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Text(
                        text = "NextDNS Profile",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Divider()
                InfoRow("Profile ID", NextDNSConfig.PROFILE_ID)
                InfoRow("DNS-over-TLS", NextDNSConfig.DNS_OVER_TLS)
                InfoRow("Primary DNS", NextDNSConfig.IPV4_DNS_SERVERS[0])
                InfoRow("Secondary DNS", NextDNSConfig.IPV4_DNS_SERVERS[1])
            }
        }

        // Device Admin Setup
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
                    Icon(Icons.Default.Security, contentDescription = null)
                    Text(
                        text = "Device Administrator",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = "Enable device administrator to make this app undeletable and protect DNS settings.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = {
                        if (isAdminActive) {
                            showAdminDialog = true
                        } else {
                            adminLauncher.launch(adminManager.requestAdminActivation())
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAdminActive) "Deactivate Admin" else "Activate Admin")
                }
                if (isAdminActive) {
                    Text(
                        text = "✓ Device administrator is active",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // DNS Configuration
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "DNS Configuration",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Configure NextDNS to protect your DNS queries and enable filtering.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = {
                        if (isDnsEnabled) {
                            scope.launch {
                                dnsManager.stopDns()
                                prefsManager.setDnsEnabled(false)
                                isDnsEnabled = false
                            }
                        } else {
                            val intent = VpnService.prepare(context)
                            if (intent != null) {
                                vpnLauncher.launch(intent)
                            } else {
                                scope.launch {
                                    dnsManager.configureDns("vpn")
                                    prefsManager.setDnsEnabled(true)
                                    isDnsEnabled = true
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isAdminActive
                ) {
                    Text(if (isDnsEnabled) "Disable DNS" else "Enable DNS")
                }
                if (!isAdminActive) {
                    Text(
                        text = "⚠ Activate device administrator first",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (isDnsEnabled) {
                    Text(
                        text = "✓ NextDNS is active",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    if (showAdminDialog) {
        AlertDialog(
            onDismissRequest = { showAdminDialog = false },
            title = { Text("Deactivate Device Admin?") },
            text = { Text("Deactivating device administrator will allow the app to be uninstalled and DNS settings to be changed. Are you sure?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        adminManager.removeAdmin()
                        isAdminActive = false
                        scope.launch {
                            prefsManager.setDeviceAdminEnabled(false)
                        }
                        showAdminDialog = false
                    }
                ) {
                    Text("Deactivate")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
