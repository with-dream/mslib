package com.ms.library.utils;

public class ErrCode {
    public static final int SUCC = 0;
    public static final int FAILED = -1;
    public static final int ERROR_SERVER = -3;
    public static final int ERROR_NET = -3;
    public static final int ERROR_NAME_OR_PWD = -100;    //用户名或密码错误
    public static final int ERROR_VER_CODE = -101;    //手机验证码错误
    public static final int ERROR_LOGIN = -102;    //登录错误
}
