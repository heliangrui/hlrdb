package com.hlr.db;

import com.hlr.db.config.PoolSnapshot;
import com.hlr.db.config.PoolType;
import com.hlr.db.tools.IDataSourceAdapter;
import com.hlr.db.tools.impl.DruidDataSourceAdapter;
import com.hlr.db.util.FileUtil;
import com.hlr.db.util.INIFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * DBConnectionPools
 * Description: 数据库连接初始化类
 * date: 2023/11/29 11:32
 *
 * @author hlr
 */
public class DBConnectionPools {
    private static final Logger logger = LoggerFactory.getLogger(DBConnectionPools.class);

    private static DBConnectionPools instance = null;
    //项目路径
    private static String appPath;
    // 项目名称
    private static String appName;

    private static AtomicBoolean closed = new AtomicBoolean(false);
    private static int watch;
    private static long watchInterval;
    // 数据库连接池对象 用于建立连接和获取连接
    private IDataSourceAdapter adapter;

    private DBConnectionPools() {
        initDBConnectionPools();
    }

    public static void setDBConfig(String appPath, String appName) {
        DBConnectionPools.appPath = appPath;
        DBConnectionPools.appName = appName;
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    public static DBConnectionPools getInstance() {
        if (instance == null) {
            synchronized (DBConnectionPools.class) {
                if (instance == null) {
                    instance = new DBConnectionPools();
                }
            }
        }
        return instance;
    }

    /**
     * 线程池销毁方法
     */
    public static void destroy() {
        if (closed.compareAndSet(false, true)) {
            DBConnectionPools instance1 = getInstance();
            instance1.adapter.shutdown();
        }
    }

    /**
     * DBConnectionPools 初始化
     */
    public void initDBConnectionPools() {
        String dbPath = initDbPath();
        try {
            createPools(dbPath);
        } catch (HlrPoolException e) {
            logger.error("initDBConnectionPools error ...", e);
        }
    }

    private void createPools(String dbPath) throws HlrPoolException {
        if (!FileUtil.isFile(dbPath)) {
            throw new HlrPoolException("");
        }

        try {
            INIFile iniFile = new INIFile(dbPath);
            // 读取config配置
            watch = iniFile.getIntegerParamData("dbconfig", "watch", 1);
            watchInterval = iniFile.getLongParamData("dbconfig", "watchInterval", 1000);
            String pool = iniFile.getParamData("config", "pool", "druid");

            initPool(pool);

            for (String dbName : iniFile.getParamKey()) {
                if (!dbName.equals("dbconfig")) {
                    // 读取数据库配置信息
                    //type=mysql/pgsql/sqlserver
                    //kmstoken= 11111111
                    //url=
                    //username=
                    //password=
                    //driver
                    //activetime=15000
                    //availablecount=0
                    //connectLifeTime= 1
                    // kmsToken 暂不处理
                    String kmstoken = iniFile.getParamData(dbName, "kmstoken");
                    String url = iniFile.getParamData(dbName, "url");
                    if (url != null) {
                        String username = iniFile.getParamData(dbName, "username", "");
                        String password = iniFile.getParamData(dbName, "password", "");
                        String driver = iniFile.getParamData(dbName, "driver", "");
                        int activetime = iniFile.getIntegerParamData(dbName, "activetime", 15000);
                        int availablecount = iniFile.getIntegerParamData(dbName, "availablecount", 0);
                        int connectLifeTime = iniFile.getIntegerParamData(dbName, "connectLifeTime", 1800000);
                        int houseKeepingSleepTime = iniFile.getIntegerParamData(dbName, "house-keeping-sleep-time", 5000);
                        int maxconn = iniFile.getIntegerParamData(dbName, "maxconn", 10);
                        int newconn = iniFile.getIntegerParamData(dbName, "newconn", 0);
                        if (newconn == 0) {
                            newconn = maxconn;
                        }
                        registerConnectionPool(dbName, url, username, password, driver, activetime, availablecount, connectLifeTime, houseKeepingSleepTime, maxconn, newconn);
                    }

                }
            }

            if (watch > 0) {
                startWatch();
            }


        } catch (Exception e) {
            logger.error("hlr-db-pool connect error。。", e);
            throw new RuntimeException(e);
        }
    }

    private void startWatch() {
        Map<String, Integer> connectionMap = new HashMap<>();
        Map<String, Integer> curActiveMap = new HashMap<>();

        new Thread(() -> {
            while (!closed.get()) {
                try {
                    Thread.sleep(watchInterval);
                } catch (InterruptedException e) {
                }

                String[] dbNames = adapter.getDbNames();
                for (String dbName : dbNames) {
                    PoolSnapshot snapshot = adapter.getSnapshot(dbName);
                    if (watch == 2 || curActiveMap.get(dbName) == null || curActiveMap.get(dbName) != snapshot.getCurActiveCount() || connectionMap.get(dbName) == null || snapshot.getConnection() != connectionMap.get(dbName)) {
                        logger.info("uudbpool:db:{},{}", dbName, snapshot);
                    }
                    curActiveMap.put(dbName, snapshot.getCurActiveCount());
                    connectionMap.put(dbName, snapshot.getConnection());
                }
            }
        }, "DRUID POOL STAT").start();

    }

    // 增加数据库连接对象
    private void registerConnectionPool(String dbName, String url, String username, String password, String driver, int activetime, int availablecount, int connectLifeTime, int houseKeepingSleepTime, int maxconn, int newconn) {

        if (dbName != null && url != null) {
            try {
                adapter.registerConnectionPool(dbName, url, username, password, maxconn, newconn, driver, houseKeepingSleepTime, activetime, availablecount, connectLifeTime);

                logger.info("hlr-db-pool: registerConnectionPool " + dbName + " maxconn:" + maxconn + " newconn:" + newconn + " availablecount:" + availablecount + " successfully!");
            } catch (Exception var13) {
                var13.printStackTrace();
                logger.error("hlr-db-pool: can't registerConnectionPool " + dbName);
            }

        } else {
            logger.info("hlr-db-pool: can't registerConnectionPool, dbName=" + dbName + ", url=" + url);
        }
    }

    // 线程池选择器
    private void initPool(String pool) {
        IDataSourceAdapter dataSourceAdapter = new DruidDataSourceAdapter();
        if (PoolType.getPoolType(pool) == PoolType.Druid && dataSourceAdapter.init()) {
            adapter = dataSourceAdapter;
        }


    }

    // 初始化db 项目路径
    private String initDbPath() {
        String thisFilePath;
        try {
            if (DBConnectionPools.appName != null && DBConnectionPools.appPath != null) {
                logger.info("hlr-db-pool appName:{},appPath:{}", DBConnectionPools.appName, DBConnectionPools.appPath);
            } else {
                URL systemResource = ClassLoader.getSystemResource("db.prop");
                String path = systemResource.getPath();
                if (path.contains(":")) {
                    path = path.substring(1);
                }

                DBConnectionPools.appName = "db";
                DBConnectionPools.appPath = path.substring(0, path.indexOf("db.prop"));
            }
            if (!DBConnectionPools.appPath.endsWith(File.separator)) {
                DBConnectionPools.appPath = DBConnectionPools.appPath + File.separator;
            }
            thisFilePath = DBConnectionPools.appPath + DBConnectionPools.appName + ".prop";
            logger.info(thisFilePath);
            return thisFilePath;
        } catch (Exception e) {
            logger.error("获取db文件错误！");
            throw new RuntimeException("获取db文件错误");
        }
    }

    /**
     * 根据 库别名 获取连接
     *
     * @param dbname
     * @return
     * @throws SQLException
     */
    public Connection getConnection(String dbname) throws SQLException {
        return adapter.getConnection(dbname);
    }
}
