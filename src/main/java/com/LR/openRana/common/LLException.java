package com.LR.openRana.common;


/**
 * 自定义异常
 *
 * @author Adam agtech-team
 * @email info@ag-tech.cn
 * @date 2020年06月30日 上午10:00:00
 */
public class LLException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public LLException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public LLException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}
