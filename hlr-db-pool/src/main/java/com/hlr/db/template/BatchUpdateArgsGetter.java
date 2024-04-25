package com.hlr.db.template;

/**
 * BatchUpdateArgsGetter
 * Description:
 * date: 2024/4/25 11:17
 *
 * @author hlr
 */
public interface BatchUpdateArgsGetter<T> {
    
    Object[] getArgs(T object);
}
