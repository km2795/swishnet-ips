# SwishNet IPS

SwishNet IPS is an Android application that provides a real-time dashboard for monitoring network traffic. It acts as an Intrusion Prevention System (IPS), allowing you to start and stop a VPN service to analyze network packets on your device.

## Features

*   **Real-time Dashboard:** A comprehensive dashboard displays key network statistics.
*   **IPS Control:** Easily start and stop the Intrusion Prevention System (IPS) service.
*   **Network Statistics:**
    *   **Total Packets:** The total number of packets that have passed through the VPN.
    *   **Threats Blocked:** The number of packets identified as threats and blocked.
    *   **Packets Received/Sent:** The total number of packets received and sent by the device.
    *   **Protocol Breakdown:** A visual breakdown of network traffic by protocol (TCP, UDP, ICMP, etc.).
    *   **System Information:** View the IPS uptime, total data processed, and the packet drop rate.
*   **Error Handling:** The UI includes states for loading and connection errors, with a retry option.

## How It Works

The app uses a `VpnService` to capture and analyze network traffic. 
The `StatsViewModel` uses a `StatsRepository` to fetch real-time statistics using the Android 
`TrafficStats` API. The protocol breakdown and threats blocked are currently using mock data, 
as this information is not available through public Android APIs.

For instructions on how to set up and run the project, please refer to the [SETUP_GUIDE.md](SETUP_GUIDE.md) file.
