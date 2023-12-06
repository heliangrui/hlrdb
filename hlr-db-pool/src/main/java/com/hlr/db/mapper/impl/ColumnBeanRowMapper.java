package com.hlr.db.mapper.impl;

import com.hlr.db.mapper.RowMapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * ColumnBeanRowMapper
 * Description:
 * date: 2023/12/5 12:02
 *
 * @author hlr
 */
public class ColumnBeanRowMapper<T> implements RowMapper<T> {

    private Class<T> requiredType;

    public ColumnBeanRowMapper(Class<T> requiredType) {
        this.requiredType = requiredType;
    }

    private static String underlineToCamel(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        String[] words = str.split("_");
        for (int i = 0; i < words.length; i++) {
            String tem = words[i];
            if (i != 0) {
                sb.append(tem.substring(0, 1).toUpperCase()).append(tem.substring(1).toLowerCase());
            } else {
                sb.append(tem);
            }
        }
        return sb.toString();
    }

    private static Object convertValue(Class<?> targetType, Object value) {
        // 根据目标类型和原始值进行类型转换  
        // 这里可以根据实际需要添加更多的类型转换逻辑  
        if (targetType == Integer.class && value instanceof String) {
            return Integer.parseInt((String) value);
        } else if (targetType == Boolean.class && value instanceof String) {
            return Boolean.parseBoolean((String) value);
        } else if (targetType == Long.class && value instanceof String) {
            return Long.parseLong((String) value);
        } else if (targetType == Float.class && value instanceof String) {
            return Float.parseFloat((String) value);
        } else if (targetType == Double.class && value instanceof String) {
            return Double.parseDouble((String) value);
        } else {
            return value; // 如果不需要转换，直接返回原始值  
        }
    }

    @Override
    public T mapRow(ResultSet rs) throws SQLException {
        try {
            T t = requiredType.newInstance();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String catalogName = metaData.getColumnLabel(i);
                if (catalogName == null || catalogName.isEmpty()) {
                    catalogName = metaData.getColumnName(i);
                }
                Field declaredField = null;
                try {
                    declaredField = requiredType.getDeclaredField(catalogName);
                } catch (Exception e) {
                    try {
                        declaredField = requiredType.getDeclaredField(underlineToCamel(catalogName));
                    } catch (Exception s) {
                    }
                }
                if (declaredField != null) {
                    declaredField.setAccessible(true);
                    Object object = rs.getObject(i, declaredField.getType());
                    declaredField.set(t, object);
                }
            }
            return t;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

}
