package com.hlr.db.tools.impl;

import com.hlr.db.tools.IDataSourceAdapter;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DruidDataSourceAdapter
 * Description:
 * date: 2023/11/30 17:08
 *
 * @author hlr
 */
public class DruidDataSourceAdapter implements IDataSourceAdapter {
    @Override
    public boolean init() {
        return false;
    }

    @Override
    public Connection getConnection(String var1) throws SQLException {
        return null;
    }

    @Override
    public void registerConnectionPool(String dbName, String url, String username, String password, int maxconn, int newconn, String driver, int houseKeepingSleepTime, int activeTime, int availablecount, int connectLifeTime) throws Exception {
        
    }


    @Override
    public String[] getDbNames() {
        return new String[0];
    }
    
    @Override
    public void shutdown() {

    }
}
