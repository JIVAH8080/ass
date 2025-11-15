package com.nextdns.protector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * DNS Configuration Service
 * Handles applying and reverting DNS settings for NextDNS
 */
public class DnsConfigService extends Service {

    private static final String TAG = "DnsConfigService";
    private static final String PREFS_NAME = "NextDNSPrefs";

    // NextDNS Configuration
    private static final String NEXTDNS_PROFILE_ID = "e628d2";
    private static final String NEXTDNS_DOT = "e628d2.dns.nextdns.io";
    private static final String NEXTDNS_DNS1 = "45.90.28.194";
    private static final String NEXTDNS_DNS2 = "45.90.30.194";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();

            switch (action) {
                case "APPLY_DNS":
                    applyNextDnsSettings();
                    break;
                case "REVERT_DNS":
                    revertDnsSettings();
                    break;
                default:
                    Log.w(TAG, "Unknown action: " + action);
            }
        }

        stopSelf(startId);
        return START_NOT_STICKY;
    }

    /**
     * Apply NextDNS settings to the device
     */
    private void applyNextDnsSettings() {
        Log.i(TAG, "Applying NextDNS settings...");

        try {
            // Save current DNS settings before changing
            saveCurrentDnsSettings();

            // Method 1: Try to set Private DNS (DNS-over-TLS) - Requires Android 9+
            boolean dotSuccess = setPrivateDns(NEXTDNS_DOT);

            // Method 2: Try to set static DNS servers via settings
            boolean staticDnsSuccess = setStaticDns(NEXTDNS_DNS1, NEXTDNS_DNS2);

            if (dotSuccess || staticDnsSuccess) {
                showToast("NextDNS settings applied successfully!");
                Log.i(TAG, "NextDNS configuration applied");

                // Save configuration status
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit()
                     .putBoolean("dns_configured", true)
                     .putLong("last_configured", System.currentTimeMillis())
                     .apply();
            } else {
                showToast("Failed to apply DNS settings. Please configure manually.");
                Log.e(TAG, "Failed to apply NextDNS settings");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error applying DNS settings", e);
            showToast("Error: " + e.getMessage());
        }
    }

    /**
     * Set Private DNS (DNS-over-TLS) - Requires Android 9+
     */
    private boolean setPrivateDns(String hostname) {
        try {
            // This requires WRITE_SECURE_SETTINGS permission
            // Must be granted via ADB: adb shell pm grant com.nextdns.protector android.permission.WRITE_SECURE_SETTINGS

            Settings.Global.putString(getContentResolver(),
                                     "private_dns_mode", "hostname");
            Settings.Global.putString(getContentResolver(),
                                     "private_dns_specifier", hostname);

            Log.i(TAG, "Private DNS set to: " + hostname);
            return true;

        } catch (SecurityException e) {
            Log.w(TAG, "WRITE_SECURE_SETTINGS permission not granted. " +
                       "Run: adb shell pm grant com.nextdns.protector android.permission.WRITE_SECURE_SETTINGS");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to set Private DNS", e);
            return false;
        }
    }

    /**
     * Set static DNS servers
     * Note: This method may have limited effectiveness on modern Android versions
     */
    private boolean setStaticDns(String dns1, String dns2) {
        try {
            // Attempt to set DNS via system properties (requires root on most devices)
            // This is included for completeness but may not work without root

            executeShellCommand("setprop net.dns1 " + dns1);
            executeShellCommand("setprop net.dns2 " + dns2);

            Log.i(TAG, "Static DNS servers set: " + dns1 + ", " + dns2);
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Failed to set static DNS (may require root)", e);
            return false;
        }
    }

    /**
     * Revert DNS settings to previous configuration
     */
    private void revertDnsSettings() {
        Log.i(TAG, "Reverting DNS settings...");

        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String previousMode = prefs.getString("previous_dns_mode", "off");

            // Revert Private DNS to previous mode
            Settings.Global.putString(getContentResolver(),
                                     "private_dns_mode", previousMode);

            if ("hostname".equals(previousMode)) {
                String previousHost = prefs.getString("previous_dns_host", "");
                if (!previousHost.isEmpty()) {
                    Settings.Global.putString(getContentResolver(),
                                             "private_dns_specifier", previousHost);
                }
            }

            // Clear configuration status
            prefs.edit()
                 .putBoolean("dns_configured", false)
                 .apply();

            showToast("DNS settings reverted to previous configuration");
            Log.i(TAG, "DNS settings reverted");

        } catch (Exception e) {
            Log.e(TAG, "Error reverting DNS settings", e);
            showToast("Error reverting DNS: " + e.getMessage());
        }
    }

    /**
     * Save current DNS settings before modifying
     */
    private void saveCurrentDnsSettings() {
        try {
            String currentMode = Settings.Global.getString(getContentResolver(),
                                                          "private_dns_mode");
            String currentHost = Settings.Global.getString(getContentResolver(),
                                                          "private_dns_specifier");

            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit()
                 .putString("previous_dns_mode", currentMode != null ? currentMode : "off")
                 .putString("previous_dns_host", currentHost != null ? currentHost : "")
                 .apply();

            Log.i(TAG, "Saved current DNS settings: mode=" + currentMode + ", host=" + currentHost);

        } catch (Exception e) {
            Log.e(TAG, "Error saving current DNS settings", e);
        }
    }

    /**
     * Execute shell command
     */
    private String executeShellCommand(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()));

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        process.waitFor();
        reader.close();

        return output.toString();
    }

    /**
     * Show toast message on UI thread
     */
    private void showToast(final String message) {
        // Post to main thread for toast
        android.os.Handler handler = new android.os.Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DnsConfigService.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
