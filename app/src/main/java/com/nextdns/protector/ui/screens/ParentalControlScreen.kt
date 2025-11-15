package com.nextdns.protector.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.SafetyCheck
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nextdns.protector.data.PreferencesManager
import kotlinx.coroutines.launch

@Composable
fun ParentalControlScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefsManager = remember { PreferencesManager(context) }
    
    val parentalControlEnabled by prefsManager.parentalControlEnabledFlow.collectAsState(initial = false)
    
    var blockPornography by remember { mutableStateOf(true) }
    var blockGambling by remember { mutableStateOf(true) }
    var blockDating by remember { mutableStateOf(false) }
    var blockPiracy by remember { mutableStateOf(true) }
    var blockSocialNetworks by remember { mutableStateOf(false) }
    var safeSearch by remember { mutableStateOf(true) }
    var youtubeRestricted by remember { mutableStateOf(true) }
    var blockBypass by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Parental Control",
            style = MaterialTheme.typography.headlineMedium
        )

        // Master Switch Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (parentalControlEnabled) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FamilyRestroom,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "Parental Control",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if (parentalControlEnabled) "Enabled" else "Disabled",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Switch(
                    checked = parentalControlEnabled,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            prefsManager.setParentalControlEnabled(enabled)
                        }
                    }
                )
            }
        }

        // Content Filtering Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Content Filtering",
                    style = MaterialTheme.typography.titleMedium
                )
                Divider()
                
                SecurityToggle(
                    title = "Block Pornography",
                    description = "Block adult content websites",
                    checked = blockPornography,
                    onCheckedChange = { blockPornography = it }
                )
                
                SecurityToggle(
                    title = "Block Gambling",
                    description = "Block gambling and betting sites",
                    checked = blockGambling,
                    onCheckedChange = { blockGambling = it }
                )
                
                SecurityToggle(
                    title = "Block Dating",
                    description = "Block dating websites and apps",
                    checked = blockDating,
                    onCheckedChange = { blockDating = it }
                )
                
                SecurityToggle(
                    title = "Block Piracy",
                    description = "Block torrent and piracy sites",
                    checked = blockPiracy,
                    onCheckedChange = { blockPiracy = it }
                )
                
                SecurityToggle(
                    title = "Block Social Networks",
                    description = "Block social media platforms",
                    checked = blockSocialNetworks,
                    onCheckedChange = { blockSocialNetworks = it }
                )
            }
        }

        // Safe Services Card
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
                    Icon(Icons.Default.SafetyCheck, contentDescription = null)
                    Text(
                        text = "Safe Services",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Divider()
                
                SecurityToggle(
                    title = "Force SafeSearch",
                    description = "Enforce safe search on Google, Bing, etc.",
                    checked = safeSearch,
                    onCheckedChange = { safeSearch = it }
                )
                
                SecurityToggle(
                    title = "YouTube Restricted Mode",
                    description = "Enable restricted mode on YouTube",
                    checked = youtubeRestricted,
                    onCheckedChange = { youtubeRestricted = it }
                )
                
                SecurityToggle(
                    title = "Block Bypass Methods",
                    description = "Block VPNs, proxies, and Tor",
                    checked = blockBypass,
                    onCheckedChange = { blockBypass = it }
                )
            }
        }

        // Info Card
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
                    text = "About Parental Controls",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Parental controls are enforced at the DNS level and work across all devices using this NextDNS profile. Changes take effect immediately.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
