package com.hlr.db.template;

import com.hlr.db.DBConnect;
import com.hlr.db.HlrPoolException;
import com.hlr.db.mapper.RowMapper;
import com.hlr.db.mapper.impl.ColumnBeanRowMapper;
import com.hlr.db.mapper.impl.ColumnMapRowMapper;
import com.hlr.db.mapper.impl.ColumnSimpleRowMapper;
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

    /**
     * 更新插入语句
     *
     * @param dbName 库别名
     * @param sql    sql
     * @param args   参数
     * @return 更新数量
     * @throws HlrPoolException
     */
    public static int update(String dbName, String sql, Object... args) throws HlrPoolException {
        return execute(dbName, sql, (connect) -> {
            setValues(connect, args);
            return connect.executeUpdate();
        }, false);
    }

    /**
     * 更新插入语句
     *
     * @param dbName 库别名
     * @param sql    dql
     * @return 更新数量
     * @throws HlrPoolException
     */
    public static int update(String dbName, String sql) throws HlrPoolException {
        return update(dbName, sql, null);
    }

    /**
     * 插入语句并返回自增id
     *
     * @param dbName 库别名
     * @param sql    sql
     * @param args   参数
     * @return 自增id
     * @throws HlrPoolException
     */
    public static long insertAndReturnAutoIncKey(String dbName, String sql, Object... args) throws HlrPoolException {
        return execute(dbName, sql, (connect) -> {
            setValues(connect, args);
            connect.executeUpdate();
            ResultSet generatedKeys = connect.getGeneratedKeys();
            return generatedKeys.next() ? generatedKeys.getLong(1) : 0;
        }, true);
    }

    /**
     * 批量更新 插入语句
     *
     * @param dbName    库别名
     * @param sql       sql
     * @param batchArgs 参数
     * @return
     * @throws HlrPoolException
     */
    public static int[] batchUpdate(String dbName, String sql, List<Object[]> batchArgs) throws HlrPoolException {
        return execute(dbName, sql, (connect) -> {
            setBatchValues(connect, batchArgs);
            return connect.executeBatch();
        }, false);
    }

    public static <T> int[] batchUpdate(String dbName, String sql, BatchUpdateArgsGetter<T> getter, List<T> data) throws HlrPoolException {
        return execute(dbName, sql, (connect) -> {

            for (T tem : data) {
                setValues(connect,getter.getArgs(tem));
                connect.addBatch();
            }
            return connect.executeBatch();
        }, false);
    }
    
    

    /**
     * 获取列必须为一列
     *
     * @param dbName    库别名
     * @param sql       sql
     * @param classType 基础类型such as：String.class,Long.class,Integer.class,Double.class...
     * @param args      参数
     * @param <T>
     * @return list
     * @throws HlrPoolException
     */
    public static <T> List<T> queryFromSimpleList(String dbName, String sql, Class<T> classType, Object... args) throws HlrPoolException {
        return queryList(dbName, sql, new ColumnSimpleRowMapper<>(classType), args);
    }

    /**
     * 获取列必须为一列
     *
     * @param dbName    库别名
     * @param sql       sql
     * @param classType 基础类型such as：String.class,Long.class,Integer.class,Double.class...
     * @param <T>
     * @return list
     * @throws HlrPoolException
     */
    public static <T> List<T> queryFromSimpleList(String dbName, String sql, Class<T> classType) throws HlrPoolException {
        return queryList(dbName, sql, new ColumnSimpleRowMapper<>(classType), null);
    }

    /**
     * 获取列必须为一列
     *
     * @param dbName    库别名
     * @param sql       sql
     * @param classType 基础类型such as：String.class,Long.class,Integer.class,Double.class...
     * @param args      参数
     * @param <T>
     * @return Object
     * @throws HlrPoolException
     */
    public static <T> T queryFromSimpleObject(String dbName, String sql, Class<T> classType, Object... args) throws HlrPoolException {
        List<T> ts = queryFromSimpleList(dbName, sql, classType, args);
        if (ts != null && ts.size() > 0) {
            return ts.get(0);
        }
        return null;
    }

    /**
     * 获取列必须为一列
     *
     * @param dbName    库别名
     * @param sql       sql
     * @param classType 基础类型such as：String.class,Long.class,Integer.class,Double.class...
     * @param <T>
     * @return list
     * @throws HlrPoolException
     */
    public static <T> T queryFromSimpleObject(String dbName, String sql, Class<T> classType) throws HlrPoolException {
        List<T> ts = queryFromSimpleList(dbName, sql, classType, null);
        if (ts != null && ts.size() > 0) {
            return ts.get(0);
        }
        return null;
    }

    /**
     * 获取一条数据对象 Map
     *
     * @param dbName 库别名
     * @param sql    sql
     * @param args   参数
     * @return map
     * @throws HlrPoolException
     */
    public static Map<String, Object> queryFromObject(String dbName, String sql, Object... args) throws HlrPoolException {
        List<Map<String, Object>> maps = queryList(dbName, sql, new ColumnMapRowMapper(), args);
        if (maps != null && maps.size() > 0) {
            return maps.get(0);
        }
        return null;
    }

    /**
     * bean实体获取
     *
     * @param dbName    库别名
     * @param sql       sql
     * @param classType bean。class
     * @param args      参数
     * @param <T>
     * @return bean
     * @throws HlrPoolException
     */
    public static <T> List<T> queryFromList(String dbName, String sql, Class<T> classType, Object... args) throws HlrPoolException {
        return queryList(dbName, sql, new ColumnBeanRowMapper<>(classType), args);
    }

    /**
     * bean实体获取
     *
     * @param dbName    库别名
     * @param sql       sql
     * @param classType bean。class
     * @param <T>
     * @return bean
     * @throws HlrPoolException
     */
    public static <T> List<T> queryFromList(String dbName, String sql, Class<T> classType) throws HlrPoolException {
        return queryList(dbName, sql, new ColumnBeanRowMapper<>(classType), null);
    }

    /**
     * bean实体获取
     *
     * @param dbName    库别名
     * @param sql       sql
     * @param classType bean。class
     * @param args      参数
     * @param <T>
     * @return bean
     * @throws HlrPoolException
     */
    public static <T> T queryFromObject(String dbName, String sql, Class<T> classType, Object... args) throws HlrPoolException {
        List<T> ts = queryList(dbName, sql, new ColumnBeanRowMapper<>(classType), args);
        if (ts != null && !ts.isEmpty()) {
            return ts.get(0);
        }
        return null;
    }

    /**
     * bean实体获取
     *
     * @param dbName    库别名
     * @param sql       sql
     * @param classType bean。class
     * @param <T>
     * @return bean
     * @throws HlrPoolException
     */
    public static <T> T queryFromObject(String dbName, String sql, Class<T> classType) throws HlrPoolException {
        List<T> ts = queryList(dbName, sql, new ColumnBeanRowMapper<>(classType), null);
        if (ts != null && !ts.isEmpty()) {
            return ts.get(0);
        }
        return null;
    }


    /**
     * @param dbName
     * @param sql
     * @param args
     * @return
     * @throws HlrPoolException
     */
    public static List<Map<String, Object>> queryList(String dbName, String sql, Object... args) throws HlrPoolException {
        return queryList(dbName, sql, new ColumnMapRowMapper(), args);
    }

    /**
     * 获取一条数据对象 Map
     *
     * @param dbName
     * @param sql
     * @return
     * @throws HlrPoolException
     */
    public static Map<String, Object> queryFromObject(String dbName, String sql) throws HlrPoolException {
        return queryFromObject(dbName, sql, (Object) null);
    }

    /**
     * 根据 RowMapper 获取获取数据
     *
     * @param dbName
     * @param sql
     * @param rowMapper
     * @param args
     * @param <T>
     * @return
     * @throws HlrPoolException
     */
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

    /**
     * 根据 RowMapper 获取获取数据
     *
     * @param dbName
     * @param sql
     * @param rowMapper
     * @param <T>
     * @return
     * @throws HlrPoolException
     */
    public static <T> List<T> queryList(String dbName, String sql, RowMapper<T> rowMapper) throws HlrPoolException {
        return queryList(dbName, sql, rowMapper, null);
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
