package com.hlr.db.transaction;

/**
 * TransactionCallback
 * Description:
 * date: 2023/12/5 14:59
 *
 * @author hlr
 */
public interface TransactionCallback<T> {
    
    T doInPreparedStatement();
}
