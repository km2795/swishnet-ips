package com.sr79.swishnet.service

import android.content.Intent
import android.net.VpnService
import android.os.Binder
import android.os.IBinder
import android.os.ParcelFileDescriptor
import com.sr79.swishnet.util.PacketHeader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

class FirewallVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var vpnThread: Thread? = null
    private val binder = LocalBinder()

    private val _stats = MutableStateFlow(VpnStats())
    val stats: StateFlow<VpnStats> = _stats

    companion object {
        const val ACTION_CONNECT = "com.sr79.swishnet.service.CONNECT"
        const val ACTION_DISCONNECT = "com.sr79.swishnet.service.DISCONNECT"
    }

    inner class LocalBinder : Binder() {
        fun getService(): FirewallVpnService = this@FirewallVpnService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_CONNECT) {
            startVpn()
        }
        return START_STICKY
    }

    private fun startVpn() {
        vpnThread = Thread {
            try {
                val builder = Builder()
                    .setSession("SwishNetVPN")
                    .addAddress("10.0.0.2", 24)
                    .addRoute("0.0.0.0", 0)
                vpnInterface = builder.establish()

                val vpnInputStream = FileInputStream(vpnInterface!!.fileDescriptor)
                val vpnOutputStream = FileOutputStream(vpnInterface!!.fileDescriptor)

                val packet = ByteBuffer.allocate(32767)
                var blockedThreats = 0L

                while (true) {
                    val length = vpnInputStream.read(packet.array())
                    if (length > 0) {
                        packet.limit(length)

                        val header = PacketHeader(packet)

                        // Simple rule: block all traffic to a specific IP address
                        if (header.destinationAddress.hostAddress == "198.51.100.1") {
                            blockedThreats++
                            _stats.value = _stats.value.copy(threatsBlocked = blockedThreats)
                            // Drop the packet
                            continue
                        }

                        vpnOutputStream.write(packet.array(), 0, length)
                        packet.clear()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        vpnThread?.start()
    }

    private fun stopVpn() {
        vpnThread?.interrupt()
        vpnInterface?.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
    }

    data class VpnStats(val threatsBlocked: Long = 0)
}
