package com.hlr.db.tools.impl;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.hlr.db.filter.DBLogTraceFilter;
import com.hlr.db.tools.IDataSourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DruidDataSourceAdapter
 * Description:
 * date: 2023/11/30 17:08
 *
 * @author hlr
 */
public class DruidDataSourceAdapter implements IDataSourceAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DruidDataSourceAdapter.class);

    private Map<String, DruidDataSource> dataSourceMap = new ConcurrentHashMap();

    @Override
    public boolean init() {
        try {
            Class<?> clazz = Class.forName("com.alibaba.druid.pool.DruidDataSource");
            return true;
        } catch (Exception var2) {
            logger.error("加载com.alibaba.druid.pool.DruidDataSource error");
            return false;
        }
    }

    @Override
    public Connection getConnection(String var1) throws SQLException {
        return dataSourceMap.get(var1).getConnection();
    }

    @Override
    public void registerConnectionPool(String dbName, String url, String username, String password, int maxconn, int newconn, String driver, int houseKeepingSleepTime, int activeTime, int availablecount, int connectLifeTime) throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setName(dbName);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaxActive(maxconn);
        dataSource.setInitialSize(availablecount);
        dataSource.setMinIdle(availablecount);
        dataSource.setMaxWait(1000L);
        if (connectLifeTime >= 1800000) {
            dataSource.setMaxEvictableIdleTimeMillis(connectLifeTime);
        }

        dataSource.setQueryTimeout(activeTime / 1000);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setTestWhileIdle(true);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url + "&rewriteBatchedStatements=true");
        List<Filter> filters = new ArrayList();
        filters.add(new DBLogTraceFilter());
        dataSource.setProxyFilters(filters);
        this.dataSourceMap.put(dbName, dataSource);
    }


    @Override
    public String[] getDbNames() {
        return dataSourceMap.keySet().toArray(new String[0]);
    }

    @Override
    public void shutdown() {
        logger.info("druid close start .....");
        Iterator<String> iterator = dataSourceMap.keySet().iterator();
        while (iterator.hasNext()) {
            dataSourceMap.get(iterator.next()).close();
        }
        logger.info("druid close end .....");

    }
}
