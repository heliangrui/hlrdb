package com.hlr.db.filter;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * DBLogTraceFilter
 * Description:
 * date: 2023/12/1 14:53
 *
 * @author hlr
 */
public class DBLogTraceFilter extends FilterEventAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DBLogTraceFilter.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy.HH:mm:ss");

    public DBLogTraceFilter() {
    }

    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
        this.init(statement);
    }

    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
        this.init(statement);
    }

    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        this.init(statement);
    }

    protected void statementExecuteBatchBefore(StatementProxy statement) {
        this.init(statement);
    }

    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        this.trace(statement);
    }

    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        this.trace(statement);
    }

    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        this.trace(statement);
    }

    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        this.trace(statement);
    }

    private void init(StatementProxy statement) {
        statement.setLastExecuteStartNano();
    }

    private void trace(StatementProxy statement) {
        statement.setLastExecuteTimeNano();
        if (!statement.getLastExecuteSql().contains("cfg4u")) {
            String sqlLog = this.buildParameters(statement);
            long rt = statement.getLastExecuteTimeNano() / 1000000L;
            if (sqlLog.startsWith("select") && rt == 0L) {
                logger.debug(sqlLog + " (" + rt + " milliseconds)");
            } else {
                logger.info(sqlLog + " (" + rt + " milliseconds)");
            }
        }
    }

    protected String buildParameters(StatementProxy statement) {
        StringBuilder sqlLog = new StringBuilder();
        String sqlStatement = statement.getLastExecuteSql();
        Map<Integer, JdbcParameter> parameters = statement.getParameters();
        if (sqlStatement != null && sqlStatement.length() > 0) {
            int parameterIndex = 0;
            StringTokenizer st = new StringTokenizer(sqlStatement, "?");

            Object value;
            while(st.hasMoreTokens()) {
                if (parameterIndex > 0) {
                    if (parameters != null) {
                        JdbcParameter parameter = (JdbcParameter)parameters.get(new Integer(parameterIndex - 1));
                        if (parameter == null) {
                            sqlLog.append("?");
                        } else {
                            value = this.getValue(parameter.getValue());
                            if (value != null) {
                                sqlLog.append(value);
                            } else {
                                sqlLog.append("?");
                            }
                        }
                    } else {
                        sqlLog.append("?");
                    }
                }

                ++parameterIndex;
                sqlLog.append(st.nextToken());
            }

            if (sqlStatement.endsWith("?") && parameterIndex > 0) {
                if (parameters != null) {
                    value = this.getValue(((JdbcParameter)parameters.get(new Integer(parameterIndex - 1))).getValue());
                    if (value != null) {
                        sqlLog.append(value);
                    } else {
                        sqlLog.append("?");
                    }
                } else {
                    sqlLog.append("?");
                }
            }

            if (sqlStatement != null && !sqlStatement.trim().endsWith(";")) {
                sqlLog.append("; ");
            }
        }

        return sqlLog.toString();
    }

    private Object getValue(Object value) {
        if (value == null) {
            return "NULL";
        } else {
            String className;
            if (value instanceof String) {
                className = (String)value;
                String nstring = className.length() > 255 ? className.substring(0, 255) + "..." : className;
                return "'" + nstring + "'";
            } else if (value instanceof Number) {
                return value;
            } else if (value instanceof Boolean) {
                return ((Boolean)value).toString();
            } else if (value instanceof Date) {
                return "'" + DATE_FORMAT.format((Date)value) + "'";
            } else {
                className = value.getClass().getName();

                for(StringTokenizer st = new StringTokenizer(className, "."); st.hasMoreTokens(); className = st.nextToken()) {
                }

                return className;
            }
        }
    }
}
