package com.sr79.swishnet.model

import com.google.gson.annotations.SerializedName

/**
 * Data models matching the Go backend stats API response
 */

data class Stats(
    val totalPackets: Long = 0,
    val packetsAnalyzed: Long = 0,
    val packetsDropped: Long = 0,
    val threatsBlocked: Long = 0,
    val packetsReceived: Long = 0,
    val packetsSent: Long = 0,
    val protocolBreakdown: ProtocolBreakdown? = null,
    val uptimeSeconds: Long = 0,
    val bytesProcessed: Long = 0
) {
    fun getDropRate(): Float {
        return if (totalPackets > 0) {
            (packetsDropped.toFloat() / totalPackets.toFloat()) * 100f
        } else 0f
    }
    
    fun getUptimeFormatted(): String {
        val hours = uptimeSeconds / 3600
        val minutes = (uptimeSeconds % 3600) / 60
        val seconds = uptimeSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    
    fun getBytesFormatted(): String {
        return when {
            bytesProcessed < 1024 -> "$bytesProcessed B"
            bytesProcessed < 1024 * 1024 -> "${bytesProcessed / 1024} KB"
            bytesProcessed < 1024 * 1024 * 1024 -> "${bytesProcessed / (1024 * 1024)} MB"
            else -> "${bytesProcessed / (1024 * 1024 * 1024)} GB"
        }
    }
}

data class ProtocolBreakdown(
    @SerializedName("tcp")
    val tcp: Long = 0,
    
    @SerializedName("udp")
    val udp: Long = 0,
    
    @SerializedName("icmp")
    val icmp: Long = 0,
    
    @SerializedName("other")
    val other: Long = 0
) {
    fun getTotal(): Long = tcp + udp + icmp + other
    
    fun getTcpPercentage(): Float {
        val total = getTotal()
        return if (total > 0) (tcp.toFloat() / total.toFloat()) * 100f else 0f
    }
    
    fun getUdpPercentage(): Float {
        val total = getTotal()
        return if (total > 0) (udp.toFloat() / total.toFloat()) * 100f else 0f
    }
    
    fun getIcmpPercentage(): Float {
        val total = getTotal()
        return if (total > 0) (icmp.toFloat() / total.toFloat()) * 100f else 0f
    }
    
    fun getOtherPercentage(): Float {
        val total = getTotal()
        return if (total > 0) (other.toFloat() / total.toFloat()) * 100f else 0f
    }
}
