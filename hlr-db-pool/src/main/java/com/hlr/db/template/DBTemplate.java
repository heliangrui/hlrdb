package com.hlr.db.template;

import com.hlr.db.DBConnect;
import com.hlr.db.HlrPoolException;
import com.hlr.db.mapper.RowMapper;
import com.hlr.db.mapper.impl.ColumnMapRowMapper;
import com.hlr.db.transaction.DBTransactions;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * DBTemplate
 * Description: db操作封装
 * date: 2023/12/4 15:00
 *
 * @author hlr
 */
public class DBTemplate {

    public static int update(String dbName, String sql, Object... args) throws HlrPoolException {
        return execute(dbName, sql, (connect) -> {
            setValues(connect, args);
            return connect.executeUpdate();
        }, false);
    }

    public static int insert(String dbName, String sql, Object... args) throws HlrPoolException {
        return update(dbName, sql, args);
    }

    public static int[] batchUpdate(String dbName, String sql, List<Object[]> batchArgs) throws HlrPoolException {
        return execute(dbName, sql, (connect) -> {
            setBatchValues(connect, batchArgs);
            return connect.executeBatch();
        }, false);
    }

    public static Map<String, Object> queryFromObject(String dbName, String sql, Object... args) throws HlrPoolException {
        List<Map<String, Object>> maps = queryList(dbName, sql, new ColumnMapRowMapper(), args);
        if (maps != null && maps.size() > 0) {
            return maps.get(0);
        }
        return null;
    }

    public static <T> List<T> queryList(String dbName, String sql, RowMapper<T> rowMapper, Object... args) throws HlrPoolException {
        return execute(dbName, sql, (connect) -> {
            setValues(connect, args);
            ResultSet resultSet = connect.executeQuery();
            List<T> result = new LinkedList<>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }
            return result;
        }, false);
    }


    private static void setBatchValues(DBConnect connect, List<Object[]> batchArgs) throws SQLException {
        if (batchArgs != null && batchArgs.size() > 0) {
            for (Object[] arg : batchArgs) {
                setValues(connect, arg);
                connect.addBatch();
            }
        }
    }


    private static void setValues(DBConnect connect, Object... args) throws SQLException {
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                Object arg = args[i];
                setValue(connect, i + 1, arg);
            }
        }
    }

    private static void setValue(DBConnect connect, int paramIndex, Object inValue) throws SQLException {
        if (inValue == null) {
            connect.setNull(paramIndex, 0);
        } else if (isStringValue(inValue.getClass())) {
            connect.setString(paramIndex, inValue.toString());
        } else if (isDateValue(inValue.getClass())) {
            connect.setTimestamp(paramIndex, new Timestamp(((Date) inValue).getTime()));
        } else if (inValue instanceof Calendar) {
            Calendar cal = (Calendar) inValue;
            connect.setTimestamp(paramIndex, new Timestamp(cal.getTime().getTime()), cal);
        } else {
            connect.setObject(paramIndex, inValue);
        }

    }

    private static boolean isStringValue(Class<?> inValueType) {
        return CharSequence.class.isAssignableFrom(inValueType) || StringWriter.class.isAssignableFrom(inValueType);
    }

    private static boolean isDateValue(Class<?> inValueType) {
        return Date.class.isAssignableFrom(inValueType) && !java.sql.Date.class.isAssignableFrom(inValueType) && !Time.class.isAssignableFrom(inValueType) && !Timestamp.class.isAssignableFrom(inValueType);
    }

    /**
     * @param dbName
     * @param sql
     * @param callback
     * @param <T>
     * @return
     */
    private static <T> T execute(String dbName, String sql, PreparedStatementCallback<T> callback, boolean autoGeneratedKeys) throws HlrPoolException {
        boolean dbClose = false;
        DBConnect fromHolder = null;
        try {
            fromHolder = DBTransactions.getFromHolder(dbName);
            if (fromHolder == null) {
                // 为空 表示为 不是事务内容
                dbClose = true;
                fromHolder = new DBConnect(dbName);
            }

            if (autoGeneratedKeys) {
                fromHolder.prepareStatement(sql, 1);
            } else {
                fromHolder.prepareStatement(sql);
            }


            return callback.doInPreparedStatement(fromHolder);
        } catch (Exception e) {
            throw new HlrPoolException("db-execute error", e);
        } finally {
            if (dbClose && fromHolder != null) {
                fromHolder.close();
            }
        }
    }


}