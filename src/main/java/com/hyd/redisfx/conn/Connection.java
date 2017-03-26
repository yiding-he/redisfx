package com.hyd.redisfx.conn;

/**
 * (description)
 * created at 17/03/20
 *
 * @author yiding_he
 */
public class Connection {

    private String name;

    private String host;

    private int port;

    private String passphase;

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
}
