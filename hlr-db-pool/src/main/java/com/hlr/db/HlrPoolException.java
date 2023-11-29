package com.hlr.db;

/**
 * HlrPoolException
 * Description:
 * date: 2023/11/29 14:00
 *
 * @author hlr
 */
public class HlrPoolException extends Exception {
    private static final long serialVersionUID = 2369171247301656537L;

    public HlrPoolException() {
    }

    public HlrPoolException(String m) {
        super(m);
    }

    public HlrPoolException(String m, Throwable t) {
        super(m, t);
    }
}
