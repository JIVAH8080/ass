package com.nextdns.protector.data

object NextDNSConfig {
    const val PROFILE_ID = "e628d2"
    
    // DNS-over-TLS/QUIC
    const val DNS_OVER_TLS = "e628d2.dns.nextdns.io"
    
    // DNS-over-HTTPS
    const val DNS_OVER_HTTPS = "https://dns.nextdns.io/e628d2"
    
    // IPv6 Addresses
    val IPV6_ADDRESSES = listOf(
        "2a07:a8c0::e6:28d2",
        "2a07:a8c1::e6:28d2"
    )
    
    // IPv4 DNS Servers (Linked IP)
    val IPV4_DNS_SERVERS = listOf(
        "45.90.28.194",
        "45.90.30.194"
    )
    
    // All DNS servers combined
    val ALL_DNS_SERVERS = IPV4_DNS_SERVERS + IPV6_ADDRESSES
}
