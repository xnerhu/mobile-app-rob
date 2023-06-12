package com.example.mobileapprob;

public class IpInfo {
    private DnsInfo dns;
    private DnsInfo edns;

    public DnsInfo getDns() {
        return dns;
    }

    public DnsInfo getEdns() {
        return edns;
    }

    public static class DnsInfo {
        private String ip;
        private String geo;

        public String getIp() {
            return ip;
        }

        public String getGeo() {
            return geo;
        }
    }
}
