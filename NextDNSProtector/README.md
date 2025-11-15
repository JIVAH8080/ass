# NextDNS Protector - Undeletable Android App

An undeletable Android application that configures and protects NextDNS settings using Device Administrator privileges. Once activated, this app cannot be uninstalled without first deactivating device admin permissions, ensuring persistent DNS protection.

## Features

- **Undeletable Protection**: Uses Android Device Admin API to prevent app uninstallation
- **NextDNS Configuration**: Automatically configures your device to use NextDNS
- **Multiple DNS Methods**: Supports DNS-over-TLS (DoT), DNS-over-HTTPS (DoH), and static DNS
- **Boot Persistence**: Automatically reapplies DNS settings after device restart
- **Status Monitoring**: Real-time status display of protection and DNS configuration
- **Easy Setup**: Simple one-tap activation process

## NextDNS Configuration

This app is pre-configured with the following NextDNS settings:

- **Profile ID**: `e628d2`
- **DNS-over-TLS/QUIC**: `e628d2.dns.nextdns.io`
- **DNS-over-HTTPS**: `https://dns.nextdns.io/e628d2`
- **IPv4 DNS Servers**:
  - Primary: `45.90.28.194`
  - Secondary: `45.90.30.194`
- **IPv6 DNS Servers**:
  - Primary: `2a07:a8c0::e6:28d2`
  - Secondary: `2a07:a8c1::e6:28d2`

## Requirements

- Android 8.0 (API 26) or higher
- ADB access for granting WRITE_SECURE_SETTINGS permission

## Installation

### Step 1: Build the APK

1. Open the project in Android Studio
2. Build → Generate Signed Bundle / APK
3. Select APK and click Next
4. Create or select a keystore
5. Build the release APK

Or use Gradle command line:
```bash
./gradlew assembleRelease
```

The APK will be generated at: `app/build/outputs/apk/release/app-release.apk`

### Step 2: Install on Device

```bash
adb install app/build/outputs/apk/release/app-release.apk
```

### Step 3: Grant WRITE_SECURE_SETTINGS Permission

This permission is required for the app to configure DNS settings:

```bash
adb shell pm grant com.nextdns.protector android.permission.WRITE_SECURE_SETTINGS
```

## Usage

### First Time Setup

1. **Launch the App**: Open "NextDNS Protector" from your app drawer

2. **Enable Device Admin**:
   - Tap "Enable Device Admin (Make Undeletable)"
   - Review the permissions and tap "Activate"
   - The app is now protected and cannot be uninstalled

3. **Apply DNS Settings**:
   - Tap "Apply NextDNS Settings"
   - DNS configuration will be applied automatically
   - The app will use DNS-over-TLS for secure DNS resolution

4. **Check Status**:
   - Tap "Check Status" to verify configuration
   - You should see:
     - Device Admin: ACTIVE ✓
     - Network: CONNECTED ✓
     - App Protection: ENABLED (Undeletable)

### Verifying DNS Configuration

To verify NextDNS is working:

1. Open your browser and visit: https://test.nextdns.io
2. You should see your configuration ID (e628d2) and status

Or use ADB:
```bash
adb shell getprop | grep dns
```

### Uninstalling the App

Since the app uses Device Admin, you must deactivate it first:

1. Go to **Settings** → **Security** → **Device Administrators**
2. Find "NextDNS Protector" and deactivate it
3. Read the warning message and confirm
4. Now you can uninstall the app normally

Or use ADB:
```bash
adb shell dpm remove-active-admin com.nextdns.protector/.AdminReceiver
adb uninstall com.nextdns.protector
```

## How It Works

### Device Admin Protection

The app uses Android's `DeviceAdminReceiver` API to register as a device administrator. When activated:

- The app cannot be uninstalled through normal means
- Users must explicitly deactivate device admin before uninstalling
- This ensures DNS settings remain protected

### DNS Configuration

The app applies DNS settings using multiple methods:

1. **Private DNS (DNS-over-TLS)**: Sets `private_dns_mode` to `hostname` and configures NextDNS endpoint
2. **Static DNS**: Falls back to setting static DNS servers if DoT is unavailable
3. **Boot Persistence**: Reapplies settings on device restart

### File Structure

```
NextDNSProtector/
├── app/
│   ├── src/main/
│   │   ├── java/com/nextdns/protector/
│   │   │   ├── MainActivity.java          # Main UI and logic
│   │   │   ├── AdminReceiver.java         # Device admin receiver
│   │   │   ├── BootReceiver.java          # Boot event handler
│   │   │   └── DnsConfigService.java      # DNS configuration service
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml      # Main UI layout
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   └── colors.xml
│   │   │   └── xml/
│   │   │       └── device_admin.xml       # Device admin policies
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

## Permissions

The app requires the following permissions:

- **INTERNET**: For network connectivity
- **ACCESS_NETWORK_STATE**: To check network status
- **CHANGE_NETWORK_STATE**: To modify network settings
- **WRITE_SECURE_SETTINGS**: To configure DNS (must be granted via ADB)
- **RECEIVE_BOOT_COMPLETED**: To reapply DNS on boot
- **BIND_DEVICE_ADMIN**: To register as device administrator

## Troubleshooting

### DNS Settings Not Applied

**Issue**: DNS settings don't apply after tapping the button

**Solution**:
1. Ensure WRITE_SECURE_SETTINGS permission is granted:
   ```bash
   adb shell pm grant com.nextdns.protector android.permission.WRITE_SECURE_SETTINGS
   ```
2. Check device admin is active
3. Verify network connectivity

### Cannot Enable Device Admin

**Issue**: Device admin activation fails

**Solution**:
- Some devices (Samsung Knox, Huawei EMUI) may restrict device admin
- Check device manufacturer settings
- Try on a different device or Android version

### App Still Can Be Uninstalled

**Issue**: App can be uninstalled despite device admin being active

**Solution**:
1. Verify device admin is actually active:
   ```bash
   adb shell dpm list-owners
   ```
2. Check that "NextDNS Protector" appears in Settings → Security → Device Administrators
3. Some Android versions may have additional uninstall restrictions

### DNS Not Working After Boot

**Issue**: DNS settings revert after restart

**Solution**:
1. Verify RECEIVE_BOOT_COMPLETED permission is granted
2. Check that the BootReceiver is registered in AndroidManifest.xml
3. Some devices may have battery optimization preventing the boot receiver
4. Disable battery optimization for this app

## Security Considerations

### Device Admin Risks

- Device Admin gives the app elevated privileges
- The app can prevent uninstallation, which could be problematic if the app malfunctions
- Always keep the source code secure and auditable

### Permissions

- WRITE_SECURE_SETTINGS is a powerful permission
- Only grant to apps you trust
- This permission allows modifying system-level settings

### Best Practices

1. Only use this app on devices you control
2. Test thoroughly before deployment
3. Provide users with clear uninstall instructions
4. Consider adding a PIN/password for deactivation
5. Keep the app updated with security patches

## Parental Control Use Case

This app is ideal for parental control scenarios:

- Parents can install and activate device admin
- Children cannot uninstall the app or change DNS settings
- NextDNS filtering and controls remain active
- Provides content filtering, safe search, and analytics

To configure NextDNS parental controls:
1. Visit https://my.nextdns.io
2. Log in to your NextDNS account
3. Configure Security, Privacy, and Parental Control settings
4. Settings apply automatically to devices using your profile

## Customization

### Changing NextDNS Profile

To use a different NextDNS profile, edit the constants in `MainActivity.java` and `DnsConfigService.java`:

```java
private static final String NEXTDNS_PROFILE_ID = "your_profile_id";
private static final String NEXTDNS_DOT = "your_profile_id.dns.nextdns.io";
private static final String NEXTDNS_DOH = "https://dns.nextdns.io/your_profile_id";
```

### Modifying UI

The UI layout is in `app/src/main/res/layout/activity_main.xml`. Customize colors in `colors.xml` and text in `strings.xml`.

## Building for Production

1. **Use ProGuard**: Enable minification in `app/build.gradle`:
   ```gradle
   buildTypes {
       release {
           minifyEnabled true
           shrinkResources true
       }
   }
   ```

2. **Sign the APK**: Use a production keystore
   ```bash
   ./gradlew assembleRelease
   ```

3. **Test thoroughly**: Test on multiple devices and Android versions

4. **Document limitations**: Clearly explain device admin requirements

## Legal & Ethical Considerations

### Responsible Use

- This app should only be used on devices you own or have authorization to manage
- Clearly disclose to device users that the app is undeletable
- Provide clear instructions for deactivation
- Use for legitimate purposes only (parental control, enterprise management, etc.)

### Disclosure

When deploying this app:
- Inform users about device admin activation
- Explain the uninstall process
- Provide contact information for support
- Include privacy policy for DNS data

## License

This project is for educational and authorized use only. Use responsibly and in compliance with applicable laws and regulations.

## Support

For issues or questions:
- Check the troubleshooting section above
- Review Android Device Admin documentation
- Visit NextDNS documentation: https://help.nextdns.io

## Credits

- NextDNS for DNS filtering and protection services
- Android Device Administration API documentation

---

**⚠️ Important**: This app prevents uninstallation through device admin. Always provide users with clear documentation on how to deactivate and uninstall if needed.
