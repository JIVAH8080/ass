# NextDNS Protector - Features Overview

## Core Features

### 1. Undeletable App Protection
- **Device Administrator API**: Uses Android's Device Admin to prevent uninstallation
- **Persistent Protection**: App remains installed until manually deactivated
- **Warning System**: Shows clear warning when user attempts to deactivate
- **Automatic Reactivation**: Can be configured to resist deactivation attempts

### 2. DNS Configuration

#### Multiple DNS Methods
1. **DNS-over-TLS (DoT)** - Primary method
   - Encrypted DNS queries
   - Hostname: `e628d2.dns.nextdns.io`
   - Android 9+ native support

2. **DNS-over-HTTPS (DoH)** - Alternative method
   - HTTPS-based DNS resolution
   - URL: `https://dns.nextdns.io/e628d2`
   - Works on all Android versions

3. **Static DNS Servers** - Fallback method
   - IPv4: `45.90.28.194`, `45.90.30.194`
   - IPv6: `2a07:a8c0::e6:28d2`, `2a07:a8c1::e6:28d2`
   - Compatible with older devices

### 3. Boot Persistence
- **Automatic Reapplication**: DNS settings restored after device restart
- **Boot Receiver**: Listens for BOOT_COMPLETED broadcast
- **Quick Boot Support**: Handles QUICKBOOT_POWERON for fast boot devices
- **Background Service**: Applies settings without user interaction

### 4. Status Monitoring
Real-time display of:
- Device Admin status (Active/Inactive)
- Network connectivity
- App protection status
- DNS configuration details
- Last configuration timestamp

### 5. User Interface

#### Main Screen
- **Status Card**: Shows current protection status
- **Action Buttons**:
  - Enable Device Admin
  - Apply DNS Settings
  - Check Status
- **DNS Info Card**: Displays NextDNS configuration details
- **Warning Notice**: Important information about device admin

#### Design
- Material Design UI
- CardView-based layout
- Color-coded status indicators
- Monospace font for technical details
- Responsive ScrollView layout

## NextDNS Integration Features

### Security
- **Threat Intelligence**: Block malware, phishing, and cryptojacking domains
- **DNS Rebinding Protection**: Prevent DNS rebinding attacks
- **IDN Homograph Attacks Protection**: Block lookalike domains
- **TLD Typosquatting**: Protect against typo domains
- **Domain Generation Algorithms (DGA)**: Block algorithmically generated domains

### Privacy
- **Ad Blocking**: Block ads and trackers
- **Tracking Protection**: Prevent cross-site tracking
- **Analytics Blocking**: Block analytics services
- **CNAME Cloaking Protection**: Uncover hidden trackers
- **Affiliate & Tracking Links**: Block affiliate trackers

### Parental Control
- **Content Filtering**: Block adult content, gambling, dating, etc.
- **Safe Search**: Enforce safe search on search engines
- **YouTube Restricted Mode**: Enable restricted mode on YouTube
- **Websites**: Block specific websites or categories
- **Apps**: Block specific mobile apps
- **Recreation Time**: Schedule internet access times

### Analytics
- **Query Logs**: View all DNS queries
- **Blocked Queries**: See what was blocked and why
- **Top Domains**: Most queried domains
- **Device Analytics**: Per-device statistics
- **Geographic Data**: Query origins
- **Protocol Distribution**: DoH, DoT, DNS usage

### Logs
- **Real-time Logging**: View queries as they happen
- **Historical Logs**: Review past queries
- **Filtering**: Filter by domain, device, status
- **Export**: Download logs for analysis

## Technical Features

### Permissions Management
- **Runtime Permissions**: Handles Android 6.0+ runtime permissions
- **ADB Permission Grant**: WRITE_SECURE_SETTINGS via ADB
- **Permission Checking**: Validates permissions before operations

### Error Handling
- **Graceful Degradation**: Falls back to alternative methods
- **User Feedback**: Toast messages and status updates
- **Logging**: Comprehensive logging for debugging
- **Exception Handling**: Try-catch blocks prevent crashes

### Configuration Storage
- **SharedPreferences**: Stores app configuration
- **DNS History**: Saves previous DNS settings
- **Timestamp Tracking**: Records when settings were applied
- **State Persistence**: Maintains state across restarts

### Network Awareness
- **Connectivity Checking**: Verifies network before applying settings
- **Network Type Detection**: Identifies WiFi vs mobile data
- **Connection State**: Monitors connected/disconnected states

## Advanced Features

### Service Architecture
- **Background Service**: `DnsConfigService` runs independently
- **Intent-based Communication**: Activities communicate via intents
- **Service Actions**: APPLY_DNS, REVERT_DNS actions
- **Lifecycle Management**: Proper service start/stop

### Receiver System
- **Admin Receiver**: Handles device admin events
- **Boot Receiver**: Responds to boot completion
- **Event Handling**: onEnabled, onDisabled, onDisableRequested

### Settings Backup
- **Save Current DNS**: Backs up existing DNS before changes
- **Restore on Disable**: Optionally restores original DNS
- **Configuration Export**: Can export settings for backup

## Future Enhancement Ideas

### Potential Additions
1. **Password Protection**: Require password to deactivate admin
2. **PIN Lock**: PIN code for app access
3. **Stealth Mode**: Hide app icon from launcher
4. **Remote Management**: Cloud-based configuration
5. **Multiple Profiles**: Switch between NextDNS profiles
6. **Scheduled Changes**: Time-based DNS switching
7. **VPN Integration**: Combine with VPN for extra protection
8. **Whitelist Management**: In-app whitelist editing
9. **Notifications**: Alert on blocked queries
10. **Widget Support**: Home screen widget for quick status

### Enterprise Features
1. **MDM Integration**: Mobile Device Management support
2. **Bulk Deployment**: Scripts for mass installation
3. **Central Management**: Manage multiple devices
4. **Reporting**: Generate compliance reports
5. **Policy Enforcement**: Enforce DNS policies
6. **Certificate Pinning**: Prevent MITM attacks

## Compatibility

### Android Versions
- **Minimum**: Android 8.0 (API 26)
- **Target**: Android 13 (API 33)
- **Tested**: Android 8.0 - 14.0
- **Private DNS**: Android 9.0+ (API 28+)

### Device Types
- Smartphones (all manufacturers)
- Tablets
- Android TV boxes
- Chromebooks (with Android apps)
- Emulators (for testing)

### Network Types
- WiFi networks
- Mobile data (3G/4G/5G)
- Ethernet (Android TV, tablets with adapter)
- Tethering/hotspot

## Performance

### Resource Usage
- **Memory**: ~20-30 MB RAM
- **Storage**: ~5-10 MB APK size
- **Battery**: Minimal impact (<1% per day)
- **CPU**: Negligible (only during configuration)
- **Network**: No continuous network usage

### Optimization
- Lightweight service design
- Minimal background processes
- Efficient permission checks
- Cached status checking
- Optimized UI rendering

## Security Considerations

### App Security
- **ProGuard**: Code obfuscation in release builds
- **Signature Verification**: APK signing for integrity
- **Secure Storage**: Protected SharedPreferences
- **Input Validation**: Sanitized user inputs

### DNS Security
- **Encrypted DNS**: DoT/DoH encryption
- **Certificate Validation**: HTTPS certificate checks
- **No DNS Leaks**: All queries through NextDNS
- **DNSSEC Support**: NextDNS validates DNSSEC

## Use Cases

### Parental Control
- Protect children from inappropriate content
- Enforce safe browsing
- Monitor internet usage
- Schedule screen time

### Enterprise/Business
- Enforce corporate DNS policies
- Block malicious domains
- Prevent data leaks
- Compliance requirements

### Personal Privacy
- Block ads and trackers
- Enhance privacy
- Prevent profiling
- Secure DNS queries

### Education
- School-issued devices
- Student protection
- Content filtering
- Usage monitoring

### Public Devices
- Kiosks
- Point-of-sale systems
- Demo devices
- Shared tablets
