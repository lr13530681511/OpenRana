package com.LR.openRana.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 异常处理器
 * 用于处理全局异常，包括自定义异常和系统异常
 * 通过@，RestControllerAdvice注解标记的类会被Spring MVC识别为异常处理器
 *
 * @author Adam
 */
@RestControllerAdvice
public class LLExceptionHandler {
    // 使用SLF4J日志框架记录日志
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 处理自定义异常
     * 当控制器中的方法抛出LLException异常时，该方法会被调用
     *
     * @param e 异常对象，不为null
     * @return 返回一个包含异常代码和消息的R对象
     */
    @ExceptionHandler(LLException.class)
    public R handleOWException(LLException e) {
        R r = new R();
        r.put("code", e.getCode());
        r.put("msg", e.getMessage());
        return r;
    }

    /**
     * 处理未找到处理器异常
     * 当请求的路径在Spring MVC中没有对应的处理器时，会抛出NoHandlerFoundException异常
     *
     * @param e 异常对象，不为null
     * @return 返回一个包含错误代码和消息的R对象，指示路径不存在
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public R handlerNoFoundException(Exception e) {
        logger.error(e.getMessage(), e);
        return R.error(404, "路径不存在，请检查路径是否正确");
    }

    /**
     * 处理其他系统异常
     * 当其他系统异常发生时，该方法会被调用
     *
     * @param e 异常对象，不为null
     * @return 返回一个包含默认错误代码和消息的R对象
     */
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return R.error();
    }
}
