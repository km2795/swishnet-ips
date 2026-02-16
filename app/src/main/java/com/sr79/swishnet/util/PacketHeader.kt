package com.sr79.swishnet.util

import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteBuffer

class PacketHeader(private val buffer: ByteBuffer) {

    val ipVersion: Int
    val protocol: Int
    val sourceAddress: InetAddress
    val destinationAddress: InetAddress

    init {
        ipVersion = buffer.get(0).toInt() ushr 4
        protocol = buffer.get(9).toInt()
        val sourceAddressBytes = ByteArray(4)
        buffer.position(12)
        buffer.get(sourceAddressBytes)
        sourceAddress = InetAddress.getByAddress(sourceAddressBytes)

        val destinationAddressBytes = ByteArray(4)
        buffer.position(16)
        buffer.get(destinationAddressBytes)
        destinationAddress = InetAddress.getByAddress(destinationAddressBytes)
    }
}
