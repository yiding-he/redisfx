package com.hyd.redisfx.conn;

/**
 * (description)
 * created at 17/03/20
 *
 * @author yiding_he
 */
public class Connection implements Cloneable {

    private String name;

    private String host;

    private int port;

    private String passphase;

    private String proxyType;

    private String proxyHost;

    private int proxyPort;

    public Connection() {
    }

    public Connection(String name, String host, int port, String passphase) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.passphase = passphase;
    }

    public String getProxyType() {
        return proxyType;
    }

    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassphase() {
        return passphase;
    }

    public void setPassphase(String passphase) {
        this.passphase = passphase;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public Connection clone() {
        try {
            return (Connection) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Connection(name, host, port, passphase);
        }
    }
}
