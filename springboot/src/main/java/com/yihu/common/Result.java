package com.yihu.common;

/*
* 统一返回包装类
* */
public class Result {

    private int code;
    private String msg;
    private Object data;
    private String token;

    public  static  Result success(){
        Result result = new Result();
        result.setCode(200);
        result.setMsg("success");
        return result;
    }

//    public  static  Result success(String token){
//        Result result = new Result();
//        result.setCode(200);
//        result.setMsg("success");
//        result.setToken(token);
//        return result;
//    }

    public  static  Result success(Object data){
        Result result = success();
        result.setData(data);
        return result;
    }

    public  static  Result success(Object data,String token){
        Result result = success();
        result.setData(data);
        result.setToken(token);
        return result;
    }

    public  static  Result error(){
        Result result = new Result();
        result.setCode(500);
        result.setMsg("error");
        return result;
    }

    public  static  Result error(int code,String msg){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
