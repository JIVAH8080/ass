package com.nextdns.protector.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Privacy
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PrivacyScreen() {
    var blockAds by remember { mutableStateOf(true) }
    var blockTrackers by remember { mutableStateOf(true) }
    var blockAnalytics by remember { mutableStateOf(true) }
    var blockAffiliate by remember { mutableStateOf(false) }
    var blockDisguised by remember { mutableStateOf(true) }
    var allowAffiliate by remember { mutableStateOf(false) }
    var nativeTracking by remember { mutableStateOf(true) }
    var anonymizeEDNS by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Privacy",
            style = MaterialTheme.typography.headlineMedium
        )

        // Blocklists Card
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
                    Icon(Icons.Default.Privacy, contentDescription = null)
                    Text(
                        text = "Blocklists",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Divider()
                
                SecurityToggle(
                    title = "Block Ads",
                    description = "Block advertising domains",
                    checked = blockAds,
                    onCheckedChange = { blockAds = it }
                )
                
                SecurityToggle(
                    title = "Block Trackers",
                    description = "Block tracking and analytics domains",
                    checked = blockTrackers,
                    onCheckedChange = { blockTrackers = it }
                )
                
                SecurityToggle(
                    title = "Block Analytics",
                    description = "Block website analytics services",
                    checked = blockAnalytics,
                    onCheckedChange = { blockAnalytics = it }
                )
                
                SecurityToggle(
                    title = "Block Affiliate Links",
                    description = "Block affiliate tracking links",
                    checked = blockAffiliate,
                    onCheckedChange = { blockAffiliate = it }
                )
                
                SecurityToggle(
                    title = "Block Disguised Trackers",
                    description = "Block third-party trackers disguised as first-party",
                    checked = blockDisguised,
                    onCheckedChange = { blockDisguised = it }
                )
            }
        }

        // Native Tracking Protection Card
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
                    Icon(Icons.Default.PrivacyTip, contentDescription = null)
                    Text(
                        text = "Native Tracking Protection",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Divider()
                
                SecurityToggle(
                    title = "Block Alexa Built-in",
                    description = "Block Amazon Alexa tracking",
                    checked = nativeTracking,
                    onCheckedChange = { nativeTracking = it }
                )
                
                Text(
                    text = "Blocks tracking from smart devices and native apps",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Advanced Privacy Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Advanced Privacy",
                    style = MaterialTheme.typography.titleMedium
                )
                Divider()
                
                SecurityToggle(
                    title = "Anonymize EDNS Client Subnet",
                    description = "Hide your IP subnet from DNS queries",
                    checked = anonymizeEDNS,
                    onCheckedChange = { anonymizeEDNS = it }
                )
                
                Text(
                    text = "Improves privacy by not sending your IP subnet to authoritative DNS servers",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Privacy Info Card
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
                    text = "Privacy Information",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "NextDNS does not log your DNS queries by default. All privacy settings are enforced at the DNS level.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
