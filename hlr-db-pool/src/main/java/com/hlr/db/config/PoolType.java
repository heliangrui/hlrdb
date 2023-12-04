package com.hlr.db.config;

/**
 * PoolType
 * Description:
 * date: 2023/11/30 17:30
 *
 * @author hlr
 */
public enum PoolType {
    Druid("druid");
    String type;

    PoolType(String type) {
        this.type = type;
    }

    public static PoolType getPoolType(String type) {
        PoolType[] values = PoolType.values();
        for (PoolType value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return Druid;
    }
}
