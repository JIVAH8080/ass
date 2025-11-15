# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep device admin receiver
-keep class com.nextdns.protector.AdminReceiver { *; }
-keep class com.nextdns.protector.BootReceiver { *; }
-keep class com.nextdns.protector.DnsConfigService { *; }

# Keep all activities
-keep class com.nextdns.protector.MainActivity { *; }
