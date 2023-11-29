package com.hlr.db;

import com.hlr.common.file.Path;
import com.hlr.db.tools.IDataSourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;


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
    // 数据库连接池对象 用于建立连接和获取连接
    private IDataSourceAdapter adapter;

    private DBConnectionPools() {
        initDBConnectionPools();
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

    public static void main(String[] args) {
        DBConnectionPools instance1 = DBConnectionPools.getInstance();
    }

    /**
     * DBConnectionPools 初始化
     */
    public void initDBConnectionPools() {
        String appPath = DBConnectionPools.appPath;
        String appName = DBConnectionPools.appName;
        String thisFilePath;
        try {
            if (appName != null && appPath != null) {
                logger.info("hlr-db-pool appName:{},appPath:{}", appName, appPath);
            } else {
                thisFilePath = Path.getFullPathRelateClass("/", DBConnectionPools.class);
                boolean isWebApp = false;
                boolean isOnejar = false;
                int index1 = thisFilePath.indexOf("WEB-INF") - 1;
                if (index1 == -2) {
                    index1 = thisFilePath.indexOf("lib") - 1;
                    if (index1 == -2) {
                        isOnejar = true;
                    }
                } else {
                    isWebApp = true;
                }

                if (index1 == -2) {
                    index1 = thisFilePath.indexOf("bin") - 1;
                }

                if (isOnejar) {
                    index1 = thisFilePath.lastIndexOf(File.separator) + 1;
                    appName = thisFilePath.substring(index1, thisFilePath.length());
                    appPath = thisFilePath + File.separator;
                } else {
                    if (index1 == -2) {
                        throw new HlrPoolException("conn't location config file path!");
                    }

                    int index2 = thisFilePath.substring(0, index1).lastIndexOf(File.separator) + 1;
                    appName = thisFilePath.substring(index2, index1);
                    if (isWebApp) {
                        appPath = thisFilePath.substring(0, index2);
                    } else {
                        appPath = thisFilePath.substring(0, index1);
                    }
                }

                DBConnectionPools.appName = appName;
                thisFilePath = appPath + appName +".prop";
                logger.info(thisFilePath);
            }
        } catch (Exception e) {

        }

    }

}
