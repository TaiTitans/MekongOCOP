package com.mekongocop.authservice.common;


public class IpAddressInfo {
    public IpAddressInfo(String ipAddress, int sendCount, String lastSendTime) {
        this.ipAddress = ipAddress;
        this.sendCount = sendCount;
        this.lastSendTime = lastSendTime;
    }

    public String ipAddress;
    public int sendCount;
    public String lastSendTime;


    // Getter, setter và constructor
}