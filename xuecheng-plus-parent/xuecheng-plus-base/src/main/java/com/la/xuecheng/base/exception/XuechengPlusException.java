package com.la.xuecheng.base.exception;

/**
 * @author LA
 * @createDate 2023-04-06-14:37
 * @description 本项目自定义异常类型
 */
public class XuechengPlusException extends RuntimeException{

    private String errMessage;

    public XuechengPlusException(){

    }

    public XuechengPlusException(String message){
        super(message);
        this.errMessage = message;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public static void cast(String message){
        throw new XuechengPlusException(message);
    }

    public static void cast(CommonError error){
        throw new XuechengPlusException(error.getErrMessage());
    }
}
