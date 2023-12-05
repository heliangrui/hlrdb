package com.hlr.db.transaction;

import com.hlr.db.DBConnect;

import java.sql.Connection;

/**
 * ConnectHolder
 * Description:
 * date: 2023/12/5 15:03
 *
 * @author hlr
 */
public class ConnectHolder {
    
    private DBConnect connection;
    
    private String dbName;
    
    private boolean isNewConnection;

    public ConnectHolder(DBConnect connection, String dbName, boolean isNewConnection) {
        this.connection = connection;
        this.dbName = dbName;
        this.isNewConnection = isNewConnection;
    }

    public DBConnect getConnection() {
        return connection;
    }

    public String getDbName() {
        return dbName;
    }

    public boolean isNewConnection() {
        return isNewConnection;
    }
}
