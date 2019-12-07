package com.yianit.exception;

/**
 * 类名称: LogisServiceException
 * 类描述: 错误封装.
 * 创建人: zhangwei
 * 创建时间: 2015年9月16日 上午11:41:48
 * 修改人: zhangwei
 * 修改时间: 2015年9月16日 上午11:41:48
 * 修改备注:
 */

public class LogisServiceException extends Exception {

    /**
     * serialVersionUID:TODO(用一句话描述这个变量表示什么).
     * 
     * @since JDK 1.7
     */

    private static final long serialVersionUID = -2489866209957445978L;

    public LogisServiceException() {
    }

    public LogisServiceException(String message) {
        super(message);
    }

    public LogisServiceException(Throwable cause) {
        super(cause);
    }

    public LogisServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
