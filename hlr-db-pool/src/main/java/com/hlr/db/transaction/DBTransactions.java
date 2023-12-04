package com.hlr.db.transaction;

import com.hlr.db.DBConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * DBTransactions
 * Description: DB 事务操作
 * date: 2023/12/4 15:56
 *
 * @author hlr
 */
public class DBTransactions {
    private static final Logger logger = LoggerFactory.getLogger(DBTransactions.class);
    private static final ThreadLocal<Map<String, DBConnect>> holder = new ThreadLocal();
    
    public static DBConnect getFromHolder(String dbName){
        Map<String, DBConnect> map = holder.get();
        return map == null ? null: map.get(dbName);
    }
    
    private static void putFromHolder(String dbName,DBConnect dbConnect){
        Map<String, DBConnect> map = holder.get();
        if(map == null){
            map = new HashMap<>();
        }
        map.put(dbName,dbConnect);
        holder.set(map);
    }
    
    private static void removeFromHolder(String dbName){
        Map<String, DBConnect> map = holder.get();
        if(map != null){
            map.remove(dbName);
            if(map.size() == 0){
                holder.remove();
            }
        }
    }
    
    
    
}
