package com.hyd.redisfx;

import com.hyd.redisfx.conn.Connection;

import java.util.List;

public class UserConfig {

  private List<Connection> connections;

  public List<Connection> getConnections() {
    return connections;
  }

  public void setConnections(List<Connection> connections) {
    this.connections = connections;
  }
}
