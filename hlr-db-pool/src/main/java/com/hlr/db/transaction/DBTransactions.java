package com.hlr.db.transaction;

import com.hlr.db.DBConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DBTransactions
 * Description: DB 事务操作
 * 同库嵌套事务不持支
 * date: 2023/12/4 15:56
 *
 * @author hlr
 */
public class DBTransactions {
    private static final Logger logger = LoggerFactory.getLogger(DBTransactions.class);
    private static final ThreadLocal<Map<String, DBConnect>> holder = new ThreadLocal<>();


    public static <T> T execute(TransactionCallback<T> transactionCallback, String... dbNames) throws SQLException {
        return (T) doExecute(transactionCallback, dbNames);
    }

    public static void execute(TransactionCallbackWithoutResult transactionCallbackWithoutResult, String... dbNames) throws SQLException {
        doExecute(transactionCallbackWithoutResult, dbNames);
    }


    private static Object doExecute(Object callback, String... dbNames) throws SQLException {
        Object result = null;
        List<ConnectHolder> holders = new ArrayList<>();

        try {
            for (String dbName : dbNames) {
                DBConnect fromHolder = getFromHolder(dbName);
                if (fromHolder == null) {
                    fromHolder = new DBConnect(dbName, false);
                    putFromHolder(dbName, fromHolder);
                    holders.add(new ConnectHolder(fromHolder, dbName, true));
                } else {
                    holders.add(new ConnectHolder(fromHolder, dbName, false));
                }
            }

            if (callback instanceof TransactionCallback) {
                result = ((TransactionCallback<?>) callback).doInPreparedStatement();
            } else {
                ((TransactionCallbackWithoutResult) callback).doInPreparedStatement();
            }
            commit(holders);

            return result;

        } catch (Exception e) {
            rollback(holders, e);
            throw new SQLException(e);
        } finally {
            close(holders);
        }


    }

    private static void close(List<ConnectHolder> holders) {
        for (ConnectHolder connectHolder : holders) {
            if (connectHolder.isNewConnection()) {
                if (connectHolder.getConnection() != null) {
                    connectHolder.getConnection().close();
                }
                removeFromHolder(connectHolder.getDbName());
                logger.debug("dbName:{} close transaction", connectHolder.getDbName());
            }
        }

    }

    private static void rollback(List<ConnectHolder> holders, Exception e) {
        for (ConnectHolder connectHolder : holders) {
            if (connectHolder.isNewConnection()) {
                logger.warn("dbName:{} rollback transaction for exception: {}", connectHolder.getDbName(), e);
                try {
                    connectHolder.getConnection().rollback();
                } catch (SQLException ex) {
                    logger.error("rollback error...", ex);
                }
            }
        }

    }

    private static void commit(List<ConnectHolder> holders) {
        for (ConnectHolder connectHolder : holders) {
            if (connectHolder.isNewConnection()) {
                try {
                    connectHolder.getConnection().commit();
                } catch (SQLException e) {
                    logger.error("DBTransactions commit error..", e);
                }
            }
        }

    }

    public static DBConnect getFromHolder(String dbName) {
        Map<String, DBConnect> map = holder.get();
        return map == null ? null : map.get(dbName);
    }

    private static void putFromHolder(String dbName, DBConnect dbConnect) {
        Map<String, DBConnect> map = holder.get();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(dbName, dbConnect);
        holder.set(map);
    }

    private static void removeFromHolder(String dbName) {
        Map<String, DBConnect> map = holder.get();
        if (map != null) {
            map.remove(dbName);
            if (map.size() == 0) {
                holder.remove();
            }
        }
    }


}
