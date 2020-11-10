package com.offcn.dycommon.enums;
//其实创建枚举项就等同于调用本类的无参构造器,而赋值了就是调用本类的有参构造器
public enum ResponseCodeEnume {
    SUCCESS(0,"操作成功"),
    FAIL(1,"服务器异常"),
    NOT_FOUND(404,"资源未找到"),
    NOT_AUTHED(403,"无权限，访问拒绝"),
    PARAM_INVAILD(400,"提交参数非法");
//   上面的是枚举,下面的是为了赋值取值
    private Integer code;
    private String msg;

    ResponseCodeEnume(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

}

