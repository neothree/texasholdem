package com.texasthree.security.login.enums;


import com.texasthree.utility.utlis.StringUtils;

import java.util.List;
import java.util.Map;

public enum LoginApp {

    PROVIDER("服务商后台"),

    MERCHANT("商户后台");

    /**
     * 描述
     */
    private String desc;


    private LoginApp(String desc) {
        this.desc = desc;
    }


    public static LoginApp getEnum(String name) {
        return (LoginApp) StringUtils.getEnum(LoginApp.values(), name);
    }

    public static List<Map<String, String>> toList() {
        return StringUtils.toList(LoginApp.values(), v -> v.getDesc());
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
