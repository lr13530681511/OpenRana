package com.LR.openRana.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 异常处理器
 *
 * @author Adam agtech-team
 * @email info@ag-tech.cn
 * @date 2020年06月30日 上午10:00:00
 */
@RestControllerAdvice
public class LLExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 处理自定义异常
     */
    @ExceptionHandler(LLException.class)
    public R handleOWException(LLException e) {
        R r = new R();
        r.put("code", e.getCode());
        r.put("msg", e.getMessage());
        return r;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public R handlerNoFoundException(Exception e) {
        logger.error(e.getMessage(), e);
        return R.error(404, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return R.error();
    }


}
