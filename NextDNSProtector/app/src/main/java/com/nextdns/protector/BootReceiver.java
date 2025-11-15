package com.nextdns.protector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Boot Receiver - Reapplies DNS settings on device boot
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
            "android.intent.action.QUICKBOOT_POWERON".equals(intent.getAction())) {

            // Reapply DNS settings after boot
            Intent serviceIntent = new Intent(context, DnsConfigService.class);
            serviceIntent.setAction("APPLY_DNS");
            context.startService(serviceIntent);

            Toast.makeText(context, "NextDNS settings reapplied after boot",
                          Toast.LENGTH_SHORT).show();
        }
    }
}
