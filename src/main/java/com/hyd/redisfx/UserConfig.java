package com.hyd.redisfx;

import com.hyd.redisfx.conn.Connection;
import java.util.List;
import lombok.Data;

@Data
public class UserConfig {

    private List<Connection> connections;

}
