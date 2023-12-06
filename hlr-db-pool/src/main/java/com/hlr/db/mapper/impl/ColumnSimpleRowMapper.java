package com.hlr.db.mapper.impl;

import com.hlr.db.mapper.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * ColumnSimpleRowMapper
 * Description: 单列值获取
 * date: 2023/12/5 10:08
 *
 * @author hlr
 */
public class ColumnSimpleRowMapper<T> implements RowMapper<T> {

    private Class<T> requiredType;

    public ColumnSimpleRowMapper(Class<T> requiredType) {
        this.requiredType = requiredType;
    }

    @Override
    public T mapRow(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        if (columnCount != 1) {
            throw new SQLException("ColumnSimpleRowMapper must be one columnCount but get " + columnCount + " columnCount!");
        }
        T object = rs.getObject(1, requiredType);
        return object;
    }
}
