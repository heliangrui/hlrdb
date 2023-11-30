package com.hlr.db.tools;

import com.sun.jndi.ldap.pool.Pool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * IDataSourceAdapter
 * Description: 数据库连接池 接口
 * date: 2023/11/29 11:36
 *
 * @author hlr
 */
public interface IDataSourceAdapter {


    boolean init();

    Connection getConnection(String var1) throws SQLException;

    void registerConnectionPool(String dbName, String url, String username, String password, int maxconn, int newconn, String driver, int houseKeepingSleepTime, int activeTime, int availablecount, int connectLifeTime) throws Exception;

    String[] getDbNames();
    

    void shutdown();
}
