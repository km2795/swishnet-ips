package com.sr79.swishnet.repository

import android.net.TrafficStats
import android.os.SystemClock
import com.sr79.swishnet.model.Stats
import com.sr79.swishnet.model.ProtocolBreakdown

class StatsRepository {

    suspend fun getStats(): Stats {
        val totalRxPackets = TrafficStats.getTotalRxPackets()
        val totalTxPackets = TrafficStats.getTotalTxPackets()
        val totalPackets = totalRxPackets + totalTxPackets

        val totalRxBytes = TrafficStats.getTotalRxBytes()
        val totalTxBytes = TrafficStats.getTotalTxBytes()
        val totalBytes = totalRxBytes + totalTxBytes

        // Mock data for stats not available from Android APIs
        val packetsAnalyzed = totalPackets
        val packetsDropped = 0L 
        val threatsBlocked = 0L
        
        // Protocol breakdown is not available from public APIs, so we'll mock it.
        val protocolBreakdown = ProtocolBreakdown(
            tcp = totalPackets * 70 / 100, // Assuming 70% TCP
            udp = totalPackets * 25 / 100, // Assuming 25% UDP
            icmp = totalPackets * 5 / 100, // Assuming 5% ICMP
            other = 0
        )

        return Stats(
            totalPackets = totalPackets,
            packetsAnalyzed = packetsAnalyzed,
            packetsDropped = packetsDropped,
            threatsBlocked = threatsBlocked,
            packetsReceived = totalRxPackets,
            packetsSent = totalTxPackets,
            protocolBreakdown = protocolBreakdown,
            uptimeSeconds = SystemClock.elapsedRealtime() / 1000,
            bytesProcessed = totalBytes
        )
    }
}
