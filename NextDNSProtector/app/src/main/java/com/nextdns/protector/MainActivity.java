package com.nextdns.protector;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main Activity for NextDNS Protector
 * Handles device admin activation and DNS configuration
 */
public class MainActivity extends Activity {

    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;

    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminComponent;
    private Button btnEnableAdmin;
    private Button btnApplyDns;
    private Button btnCheckStatus;
    private TextView txtStatus;
    private TextView txtDnsInfo;

    // NextDNS Configuration
    private static final String NEXTDNS_PROFILE_ID = "e628d2";
    private static final String NEXTDNS_DOT = "e628d2.dns.nextdns.io";
    private static final String NEXTDNS_DOH = "https://dns.nextdns.io/e628d2";
    private static final String NEXTDNS_DNS1 = "45.90.28.194";
    private static final String NEXTDNS_DNS2 = "45.90.30.194";
    private static final String NEXTDNS_IPV6_1 = "2a07:a8c0::e6:28d2";
    private static final String NEXTDNS_IPV6_2 = "2a07:a8c1::e6:28d2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Device Policy Manager
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, AdminReceiver.class);

        // Initialize UI components
        btnEnableAdmin = findViewById(R.id.btnEnableAdmin);
        btnApplyDns = findViewById(R.id.btnApplyDns);
        btnCheckStatus = findViewById(R.id.btnCheckStatus);
        txtStatus = findViewById(R.id.txtStatus);
        txtDnsInfo = findViewById(R.id.txtDnsInfo);

        // Display NextDNS configuration info
        displayDnsInfo();

        // Set up button listeners
        btnEnableAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDeviceAdmin();
            }
        });

        btnApplyDns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyDnsSettings();
            }
        });

        btnCheckStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStatus();
            }
        });

        // Check initial status
        updateAdminStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAdminStatus();
    }

    /**
     * Enable device admin to make app undeletable
     */
    private void enableDeviceAdmin() {
        if (devicePolicyManager.isAdminActive(adminComponent)) {
            Toast.makeText(this, "Device Admin already enabled!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Enable Device Admin to protect NextDNS settings and prevent app uninstallation. " +
                "This will make the app undeletable until you manually deactivate device admin.");
        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
    }

    /**
     * Apply NextDNS settings via service
     */
    private void applyDnsSettings() {
        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            Toast.makeText(this, "Please enable Device Admin first!", Toast.LENGTH_LONG).show();
            return;
        }

        Intent serviceIntent = new Intent(this, DnsConfigService.class);
        serviceIntent.setAction("APPLY_DNS");
        startService(serviceIntent);

        Toast.makeText(this, "Applying NextDNS settings...", Toast.LENGTH_SHORT).show();

        // Update status after a delay
        txtStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkStatus();
            }
        }, 2000);
    }

    /**
     * Check and display current status
     */
    private void checkStatus() {
        boolean isAdminActive = devicePolicyManager.isAdminActive(adminComponent);
        boolean isConnected = isNetworkConnected();

        StringBuilder status = new StringBuilder();
        status.append("🔒 Device Admin: ").append(isAdminActive ? "ACTIVE ✓" : "INACTIVE ✗").append("\n");
        status.append("🌐 Network: ").append(isConnected ? "CONNECTED ✓" : "DISCONNECTED ✗").append("\n");
        status.append("🛡️ App Protection: ").append(isAdminActive ? "ENABLED (Undeletable)" : "DISABLED").append("\n");
        status.append("\n");

        if (isAdminActive) {
            status.append("✓ App is protected and cannot be uninstalled\n");
            status.append("✓ NextDNS settings are active\n");
            status.append("\nTo uninstall: Go to Settings → Security → Device Administrators → Deactivate this app");
        } else {
            status.append("⚠️ App can be uninstalled\n");
            status.append("⚠️ Enable Device Admin for protection");
        }

        txtStatus.setText(status.toString());
    }

    /**
     * Display NextDNS configuration information
     */
    private void displayDnsInfo() {
        StringBuilder info = new StringBuilder();
        info.append("📋 NextDNS Configuration\n");
        info.append("━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        info.append("Profile ID: ").append(NEXTDNS_PROFILE_ID).append("\n\n");
        info.append("DNS-over-TLS/QUIC:\n");
        info.append("  ").append(NEXTDNS_DOT).append("\n\n");
        info.append("DNS-over-HTTPS:\n");
        info.append("  ").append(NEXTDNS_DOH).append("\n\n");
        info.append("IPv4 DNS Servers:\n");
        info.append("  ").append(NEXTDNS_DNS1).append("\n");
        info.append("  ").append(NEXTDNS_DNS2).append("\n\n");
        info.append("IPv6 DNS Servers:\n");
        info.append("  ").append(NEXTDNS_IPV6_1).append("\n");
        info.append("  ").append(NEXTDNS_IPV6_2).append("\n");

        txtDnsInfo.setText(info.toString());
    }

    /**
     * Update device admin button status
     */
    private void updateAdminStatus() {
        boolean isAdminActive = devicePolicyManager.isAdminActive(adminComponent);

        if (isAdminActive) {
            btnEnableAdmin.setText("Device Admin Enabled ✓");
            btnEnableAdmin.setEnabled(false);
            btnApplyDns.setEnabled(true);
        } else {
            btnEnableAdmin.setText("Enable Device Admin (Make Undeletable)");
            btnEnableAdmin.setEnabled(true);
            btnApplyDns.setEnabled(false);
        }

        checkStatus();
    }

    /**
     * Check network connectivity
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Device Admin enabled! App is now undeletable.",
                             Toast.LENGTH_LONG).show();
                updateAdminStatus();

                // Automatically apply DNS settings
                applyDnsSettings();
            } else {
                Toast.makeText(this, "Device Admin activation cancelled. App can be uninstalled.",
                             Toast.LENGTH_LONG).show();
            }
        }
    }
}
