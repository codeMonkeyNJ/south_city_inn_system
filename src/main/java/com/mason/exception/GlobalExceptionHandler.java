package com.mason.exception;

import com.mason.domain.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler
    public Result handleException(BusinessException e, HttpServletResponse  response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String msg = e.getMsg();
        log.error(msg);
        return Result.error("400",msg);
    }
    /**
     * 权限异常处理
     */
    @ExceptionHandler
    public Result handleException(AuthorityException e, HttpServletResponse  response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String msg = e.getMsg();
        log.error(msg);
        return Result.error("403",msg);
    }
    /**
     * 数据库键重复异常处理
     */
    @ExceptionHandler
    public Result handleException(DuplicateKeyException e, HttpServletResponse  response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String msg = e.getMostSpecificCause().getMessage();
        Pattern mysqlPat = Pattern.compile("Duplicate entry '(.*?)' for key");
        Matcher mysqlMat = mysqlPat.matcher(msg);
        if (mysqlMat.find()) {
            msg = "'"+mysqlMat.group(1)+"'已存在";
        }
        log.error(msg);
        return Result.error("400",msg);
    }
    /**
     * 其他异常处理
     */
    @ExceptionHandler
    public Result handleException(Exception e, HttpServletResponse  response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        log.error("服务器异常", e);
        return Result.error("服务器异常");
    }

}
