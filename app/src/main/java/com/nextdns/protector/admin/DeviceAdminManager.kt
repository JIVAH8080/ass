package com.nextdns.protector.admin

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class DeviceAdminManager(private val context: Context) {

    private val devicePolicyManager: DevicePolicyManager =
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    private val componentName: ComponentName =
        ComponentName(context, NextDNSDeviceAdminReceiver::class.java)

    fun isAdminActive(): Boolean {
        return devicePolicyManager.isAdminActive(componentName)
    }

    fun requestAdminActivation(): Intent {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "Enable device administrator to protect NextDNS configuration and prevent unauthorized uninstallation. " +
                    "This will make the app undeletable until you manually deactivate device admin."
        )
        return intent
    }

    fun removeAdmin() {
        if (isAdminActive()) {
            devicePolicyManager.removeActiveAdmin(componentName)
        }
    }

    fun lockNow() {
        if (isAdminActive()) {
            devicePolicyManager.lockNow()
        }
    }
}
