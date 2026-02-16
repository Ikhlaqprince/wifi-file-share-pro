package com.wifisharepro.utils

import java.net.Inet4Address
import java.net.NetworkInterface

object NetworkUtils {

    fun getLocalIpAddress(): String? {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        for (intf in interfaces) {
            val addrs = intf.inetAddresses
            for (addr in addrs) {
                if (!addr.isLoopbackAddress && addr is Inet4Address) {
                    return addr.hostAddress
                }
            }
        }
        return null
    }
}
