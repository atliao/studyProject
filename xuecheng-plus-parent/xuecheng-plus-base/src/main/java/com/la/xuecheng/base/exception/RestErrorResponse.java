package com.la.xuecheng.base.exception;

import java.io.Serializable;

/**
 * @author LA
 * @createDate 2023-04-06-14:35
 * @description 和前端约定，返回的异常信息
 */
public class RestErrorResponse implements Serializable {

    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage = errMessage;
    }

    public String getErrMessage(){
        return errMessage;
    }
}
