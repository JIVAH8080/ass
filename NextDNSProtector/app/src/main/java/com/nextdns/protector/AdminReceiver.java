package com.nextdns.protector;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Device Admin Receiver - Prevents app uninstallation
 * When activated, this makes the app undeletable without first deactivating device admin
 */
public class AdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(context, "NextDNS Protector Enabled - App is now protected",
                      Toast.LENGTH_LONG).show();

        // Apply DNS settings when admin is enabled
        Intent serviceIntent = new Intent(context, DnsConfigService.class);
        serviceIntent.setAction("APPLY_DNS");
        context.startService(serviceIntent);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        // This message is shown when user tries to deactivate device admin
        return "WARNING: Disabling device admin will remove DNS protection and allow app uninstallation. " +
               "Your DNS settings will be reverted to default.";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(context, "NextDNS Protector Disabled - App can now be uninstalled",
                      Toast.LENGTH_LONG).show();

        // Optionally revert DNS settings when admin is disabled
        Intent serviceIntent = new Intent(context, DnsConfigService.class);
        serviceIntent.setAction("REVERT_DNS");
        context.startService(serviceIntent);
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
    }
}
