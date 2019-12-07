package com.yianit.exception;

/**
 * 类名称: LogisHandleException
 * 类描述: 统一错误封装.
 * 创建人: zhangwei
 * 创建时间: 2015年9月9日 下午2:43:20
 * 修改人: zhangwei
 * 修改时间: 2015年9月9日 下午2:43:20
 * 修改备注:
 */

public class LogisHandleException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 5453411965256962031L;

    public LogisHandleException() {
    }

    public LogisHandleException(String message) {
        super(message);
    }

    public LogisHandleException(Throwable cause) {
        super(cause);
    }

    public LogisHandleException(String message, Throwable cause) {
        super(message, cause);
    }

}
