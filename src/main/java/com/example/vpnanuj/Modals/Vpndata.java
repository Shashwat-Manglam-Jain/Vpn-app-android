package com.example.vpnanuj.Modals;

// Vpndata.java
public class Vpndata {
    private String countryname;



    private String image;

    public Vpndata(String countryname, String image, String ip, Integer ping, Integer port, String vpnProvidername) {
        this.countryname = countryname;
        this.image = image;
        this.ip = ip;
        this.ping = ping;
        this.Port = port;
        this.vpnProvidername = vpnProvidername;
    }

    public String getCountryname() {
        return countryname;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPing() {
        return ping;
    }

    public void setPing(Integer ping) {
        this.ping = ping;
    }

    public Integer getPort() {
        return Port;
    }

    public void setPort(Integer port) {
        Port = port;
    }

    public String getVpnProvidername() {
        return vpnProvidername;
    }

    public void setVpnProvidername(String vpnProvidername) {
        this.vpnProvidername = vpnProvidername;
    }

    private Integer ping;
    private String vpnProvidername;
    private String ip;
private Integer Port;

}

