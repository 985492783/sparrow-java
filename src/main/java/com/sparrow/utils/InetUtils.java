package com.sparrow.utils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author 985492783@qq.com
 * @date 2024/6/23 23:19
 */
public class InetUtils {
    private static final boolean PREFER_IPV6_ADDRESSES = Boolean.parseBoolean(
            System.getProperty("java.net.preferIPv6Addresses"));
    
    private static String selfIp = null;
    public static InetAddress findFirstNonLoopbackAddress() {
        InetAddress result = null;
        
        try {
            int lowest = Integer.MAX_VALUE;
            for (Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
                    nics.hasMoreElements(); ) {
                NetworkInterface ifc = nics.nextElement();
                if (ifc.isUp()) {
                    if (ifc.getIndex() < lowest || result == null) {
                        lowest = ifc.getIndex();
                    } else {
                        continue;
                    }
                    
                    for (Enumeration<InetAddress> addrs = ifc.getInetAddresses(); addrs.hasMoreElements(); ) {
                        InetAddress address = addrs.nextElement();
                        boolean isLegalIpVersion =
                                PREFER_IPV6_ADDRESSES ? address instanceof Inet6Address
                                        : address instanceof Inet4Address;
                        if (isLegalIpVersion && !address.isLoopbackAddress()) {
                            result = address;
                        }
                    }
                    
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        if (result != null) {
            return result;
        }
        
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static String getSelfIp() {
        if (selfIp == null) {
            selfIp = findFirstNonLoopbackAddress().getHostAddress();
        }
        return selfIp;
    }
    
}
