package com.hlr.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper
 * Description:
 * date: 2023/12/4 17:26
 *
 * @author hlr
 */
public interface RowMapper<T> {
    T mapRow(ResultSet rs) throws SQLException;
}
