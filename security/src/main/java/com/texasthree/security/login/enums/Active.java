package com.texasthree.security.login.enums;


import com.texasthree.utility.utlis.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Active {
    /**
     * 激活
     */
    ACTIVE("激活"),

    UNACTIVE("冻结");

    /**
     * 描述
     */
    private String desc;

    private Active(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static Map<String, String> toMap() {
        Active[] ary = Active.values();
        Map<String, String> enumMap = new HashMap<String, String>();
        for (int num = 0; num < ary.length; num++) {
            enumMap.put(ary[num].name(), ary[num].getDesc());
        }
        return enumMap;
    }

    public static List<Map<String, String>> toList() {
        return StringUtils.toList(Active.values(), v -> v.getDesc());
    }

}
