package com.la.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-06-14:44
 * @description
 */
@Slf4j
@ControllerAdvice
//@RestControllerAdvice //@ResponseBody+@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 对项目的自定义异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(XuechengPlusException.class) //异常处理
    @ResponseBody //以json格式返回
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //需返回状态码
    public RestErrorResponse customException(XuechengPlusException e){

        //记录异常
        log.error("系统异常:{}", e.getErrMessage(), e);

        //解析出异常信息
        String errMessage = e.getErrMessage();
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }

    /**
     * 对系统异常进行处理
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class) //异常处理
    @ResponseBody //以json格式返回
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //需返回状态码
    public RestErrorResponse customException(Exception e){

        //记录异常
        log.error("系统异常:{}", e.getMessage(), e);

        //解析出异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
        return restErrorResponse;
    }

    /**
     * 对validation异常进行处理
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class) //异常处理
    @ResponseBody //以json格式返回
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //需返回状态码
    public RestErrorResponse customException(MethodArgumentNotValidException e){

        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder stringBuilder = new StringBuilder();
        int i;
        for(i = 0; i < fieldErrors.size()-1; i++){
            FieldError fieldError = fieldErrors.get(i);
            stringBuilder.append(fieldError.getDefaultMessage() + ",");
        }
        stringBuilder.append(fieldErrors.get(i).getDefaultMessage());

        //记录异常
        log.error("系统异常:{}", e.getMessage(), stringBuilder);

        //解析出异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(stringBuilder.toString());
        return restErrorResponse;
    }
}
