package com.henglong.cloud.util;

public class API {

    /**
     * 成功
     * @return
     */
    public static Json Success(){
        Json<Object> json = new Json<>();
        json.setState(CodeConstant.SUCCESS);
        json.setMsg(MessageUtils.get("currency.success"));
        return json;
    }

    /**
     * 成功，自定义状态码
     * @param state
     * @param msg
     * @param o
     * @return
     */
    public static Json Success(Integer state, String msg, Object o){
        Json<Object> json = new Json<>();
        json.setState(state);
        json.setMsg(msg);
        json.setData(o);
        return json;
    }

    /**
     * 成功,自定义还回数据
     * @param date
     * @return
     */
    public static Json Success(Object date){
        Json<Object> json = new Json<>();
        json.setState(CodeConstant.SUCCESS);
        json.setMsg(MessageUtils.get("currency.success"));
        json.setData(date);
        return json;
    }

    /**
     * 成功，自定义信息
     * @param msg
     * @param date
     * @return
     */
    public static Json Success(String msg, Object date){
        Json<Object> json = new Json<>();
        json.setState(CodeConstant.SUCCESS);
        json.setMsg(msg);
        json.setData(date);
        return json;
    }

    /**
     * 错误
     * @return
     */
    public static Json error(){
        Json<Object> json = new Json<>();
        json.setState(CodeConstant.ERROR);
        json.setMsg(MessageUtils.get("currency.error"));
        return json;
    }

    /**
     * 通用错误，自定义信息
     * @param msg
     * @return
     */
    public static Json error(String msg){
        Json<Object> json = new Json<>();
        json.setState(CodeConstant.ERROR);
        json.setMsg(msg);
        return json;
    }

    /**
     * 自定义状态码，和信息
     * @param state
     * @param msg
     * @return
     */
    public static Json error(Integer state,String msg){
        Json<Object> json = new Json<>();
        json.setState(state);
        json.setMsg(msg);
        return json;
    }

    public static Json error(Object date){
        Json<Object> json = new Json<>();
        json.setState(CodeConstant.ERROR);
        json.setMsg(MessageUtils.get("currency.error"));
        json.setData(date);
        return json;
    }

    /**
     * 登录失败
     * @return
     */
    public static Json login_no(){
        Json<Object> json = new Json<>();
        json.setState(CodeConstant.LOGIN_NO);
        json.setMsg(MessageUtils.get("login.ok"));
        return json;
    }

    /**
     * 登录成功
     * @return
     */
    public static Json login_ok(){
        Json<Object> json = new Json<>();
        json.setState(CodeConstant.LOGIN_OK);
        json.setMsg(MessageUtils.get("login.ok"));
        return json;
    }

}
