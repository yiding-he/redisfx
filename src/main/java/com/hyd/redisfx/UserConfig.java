package com.hyd.redisfx;

import com.hyd.redisfx.conn.Connection;
import lombok.Data;

import java.util.List;

@Data
public class UserConfig {

    private List<Connection> connections;

}
