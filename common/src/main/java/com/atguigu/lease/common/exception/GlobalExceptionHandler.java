package com.atguigu.lease.common.exception;

import com.atguigu.lease.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//定义全局异常处理器，可捕获控制层抛出的异常
@ControllerAdvice
public class GlobalExceptionHandler {

    //当控制层抛出Exception异常时会被该方法捕获，并执行该方法
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        //最终返回该结果给前端
        return Result.fail();
    }

    @ExceptionHandler(LeaseException.class)
    @ResponseBody
    public Result error(LeaseException e) {
        //最终返回该结果给前端
        return Result.fail(e.getCode(), e.getMessage());
    }
}
