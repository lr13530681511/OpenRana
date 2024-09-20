package com.LR.openRana.common;

/**
 * 自定义异常类，继承自RuntimeException，用于处理应用级别的异常。
 * 这个类允许在抛出异常时指定详细的错误信息和HTTP状态码。
 *
 * @author Adam
 */
public class LLException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    // 异常信息
    private String msg;
    // 异常对应的HTTP状态码，默认为500
    private int code = 500;

    /**
     * 构造函数，使用指定的错误信息创建一个LLException对象。
     *
     * @param msg 异常的详细信息
     */
    public LLException(String msg) {
        super(msg);
        this.msg = msg;
    }

    /**
     * 构造函数，使用指定的错误信息和HTTP状态码创建一个LLException对象。
     *
     * @param msg  异常的详细信息
     * @param code 异常对应的HTTP状态码
     */
    public LLException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    /**
     * 获取异常信息。
     *
     * @return 异常的详细信息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置异常信息。
     *
     * @param msg 异常的详细信息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取异常对应的HTTP状态码。
     *
     * @return 异常的HTTP状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置异常对应的HTTP状态码。
     *
     * @param code 异常的HTTP状态码
     */
    public void setCode(int code) {
        this.code = code;
    }
}
