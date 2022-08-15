package com.texasthree.utility.utlis;

import javax.servlet.http.HttpServletRequest;

/**
 * 常用获取客户端信息的工具
 */
public final class NetUtils {

    private static String[] ipHeaders = new String[]{
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     */
    public final static String getRemoteAddr(HttpServletRequest request) {

        for (int i = 0; i < ipHeaders.length; i++) {
            String ip = request.getHeader(ipHeaders[i]);
            if (!isUnknown(ip)) {
                return getFirst(ip);
            }
        }

        return request.getRemoteAddr();
    }

    private static boolean isUnknown(String str) {
        return StringUtils.isEmpty(str) || "unknown".equalsIgnoreCase(str);
    }

    private static String getFirst(String str) {
        String[] s = str.split(",");
        for (int index = 0; index < s.length; index++) {
            String strIp = s[index].trim();
            if (!isUnknown(strIp)) {
                return strIp;
            }
        }
        return s[0].trim();
    }
}
