package com.hlr.db.transaction;

/**
 * TransationCallbackWithoutResult
 * Description:
 * date: 2023/12/5 15:10
 *
 * @author hlr
 */
public interface TransactionCallbackWithoutResult {
    void doInPreparedStatement();
}
