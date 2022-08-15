package com.texasthree.security;

public class SecurityException extends RuntimeException {

    public static final SecurityException PASSWORD_ERROR = new SecurityException(4, "密码错误");

    public static final SecurityException NAME_EXISTS = new SecurityException(5, "名称已经存在");

    public static final SecurityException USERNAME_EXISTS = new SecurityException(7, "用户名已经存在");

    public static final SecurityException AUTH_NOT_PASS = new SecurityException(107, "用户禁止登录");

    public static final SecurityException USERNAME_PASSWORD_ERROR = new SecurityException(108, "用户名或密码错误");

    public static final SecurityException IP_NOT_ALLOW = new SecurityException(109, "Invalid IP");

    public static final SecurityException G_AUTH_ERROR = new SecurityException(110, "谷歌验证错误");

    public static final SecurityException FREQUENTLY = new SecurityException(111, "登陆过于频繁，请稍后再试");

    public static final SecurityException ROLE_DELETE_WITH_NO_OPERATOR = new SecurityException(1004, "请先解除关联到此角色的操作员");

    public static final SecurityException MENU_DELETE_HAS_CHILD = new SecurityException(1005, "删除菜单还有子菜单，不能删除");


    /**
     * 具体异常码
     */
    private int code;

    private SecurityException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public SecurityException newInstance() {
        return new SecurityException(this.code, this.getMessage());
    }

    public int getCode() {
        return code;
    }
}
