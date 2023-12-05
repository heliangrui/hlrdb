package com.hlr.db.config;

/**
 * PoolSnapshot
 * Description:
 * date: 2023/12/5 15:40
 *
 * @author hlr
 */
public class PoolSnapshot {
    private int curActiveCount;
    private int availableCount;
    private int maxCount;
    private int connection;
    private int offline;
    private long refused;
    private long serverd;

    @Override
    public String toString() {
        return "PoolSnapshot{" +
                "curActiveCount=" + curActiveCount +
                ", availableCount=" + availableCount +
                ", maxCount=" + maxCount +
                ", connection=" + connection +
                ", offline=" + offline +
                ", refused=" + refused +
                ", serverd=" + serverd +
                '}';
    }

    public int getCurActiveCount() {
        return curActiveCount;
    }

    public void setCurActiveCount(int curActiveCount) {
        this.curActiveCount = curActiveCount;
    }

    public int getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getConnection() {
        return connection;
    }

    public void setConnection(int connection) {
        this.connection = connection;
    }

    public int getOffline() {
        return offline;
    }

    public void setOffline(int offline) {
        this.offline = offline;
    }

    public long getRefused() {
        return refused;
    }

    public void setRefused(long refused) {
        this.refused = refused;
    }

    public long getServerd() {
        return serverd;
    }

    public void setServerd(long serverd) {
        this.serverd = serverd;
    }
}
