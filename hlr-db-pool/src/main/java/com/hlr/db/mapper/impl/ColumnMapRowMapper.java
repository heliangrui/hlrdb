package com.hlr.db.mapper.impl;

import com.hlr.db.mapper.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ColumnMapRowMapper
 * Description:
 * date: 2023/12/4 17:28
 *
 * @author hlr
 */
public class ColumnMapRowMapper implements RowMapper<Map<String, Object>> {
    @Override
    public Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 1; i <= columnCount; i++) {
            String catalogName = metaData.getColumnLabel(i);
            if (catalogName == null || catalogName.isEmpty()) {
                catalogName = metaData.getColumnName(i);
            }
            Object object = rs.getObject(i);
            map.put(catalogName, object);
        }
        return map;
    }
}
