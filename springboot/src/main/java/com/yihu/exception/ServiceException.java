package com.yihu.exception;

public class ServiceException extends RuntimeException{

    private String code;

    public String getCode() {
        return code;
    }

    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(String code, String msg) {
        super(msg);
        this.code = code;
    }

}
