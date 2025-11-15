package com.nextdns.protector.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nextdns.protector.admin.DeviceAdminManager
import com.nextdns.protector.data.PreferencesManager

@Composable
fun SecurityScreen() {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }
    val adminManager = remember { DeviceAdminManager(context) }
    
    var blockMalware by remember { mutableStateOf(true) }
    var blockPhishing by remember { mutableStateOf(true) }
    var blockTrackers by remember { mutableStateOf(true) }
    var cryptojacking by remember { mutableStateOf(true) }
    var dnsRebinding by remember { mutableStateOf(true) }
    var idnHomographs by remember { mutableStateOf(true) }
    var typosquatting by remember { mutableStateOf(true) }
    var dga by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Security",
            style = MaterialTheme.typography.headlineMedium
        )

        // Threat Protection Card
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
                        text = "Threat Protection",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Divider()
                
                SecurityToggle(
                    title = "Block Malware",
                    description = "Block domains associated with malware",
                    checked = blockMalware,
                    onCheckedChange = { blockMalware = it }
                )
                
                SecurityToggle(
                    title = "Block Phishing",
                    description = "Block phishing and deceptive sites",
                    checked = blockPhishing,
                    onCheckedChange = { blockPhishing = it }
                )
                
                SecurityToggle(
                    title = "Block Trackers",
                    description = "Block known tracking domains",
                    checked = blockTrackers,
                    onCheckedChange = { blockTrackers = it }
                )
            }
        }

        // Advanced Security Card
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
                    Icon(Icons.Default.Shield, contentDescription = null)
                    Text(
                        text = "Advanced Security",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Divider()
                
                SecurityToggle(
                    title = "Cryptojacking Protection",
                    description = "Block cryptocurrency mining scripts",
                    checked = cryptojacking,
                    onCheckedChange = { cryptojacking = it }
                )
                
                SecurityToggle(
                    title = "DNS Rebinding Protection",
                    description = "Protect against DNS rebinding attacks",
                    checked = dnsRebinding,
                    onCheckedChange = { dnsRebinding = it }
                )
                
                SecurityToggle(
                    title = "IDN Homograph Attacks",
                    description = "Block internationalized domain name attacks",
                    checked = idnHomographs,
                    onCheckedChange = { idnHomographs = it }
                )
                
                SecurityToggle(
                    title = "Typosquatting Protection",
                    description = "Block domains that mimic popular sites",
                    checked = typosquatting,
                    onCheckedChange = { typosquatting = it }
                )
                
                SecurityToggle(
                    title = "DGA Domain Protection",
                    description = "Block algorithmically generated domains",
                    checked = dga,
                    onCheckedChange = { dga = it }
                )
            }
        }

        // Device Protection Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (adminManager.isAdminActive()) {
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
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = if (adminManager.isAdminActive()) {
                            "Device Protection Active"
                        } else {
                            "Device Protection Inactive"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (adminManager.isAdminActive()) {
                            "App is protected from uninstallation"
                        } else {
                            "Enable device admin in Setup"
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun SecurityToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
