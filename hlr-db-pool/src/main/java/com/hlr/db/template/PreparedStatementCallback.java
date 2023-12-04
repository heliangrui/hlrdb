package com.hlr.db.template;

import com.hlr.db.DBConnect;

/**
 * PreparedStatementCallback
 * Description: DBTemplate db操作
 * date: 2023/12/4 16:25
 *
 * @author hlr
 */
public interface PreparedStatementCallback<T> {
    
    T doInPreparedStatement(DBConnect dbConnect) throws Exception;

}
