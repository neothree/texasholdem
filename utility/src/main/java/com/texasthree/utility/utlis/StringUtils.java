/*
 * Copyright 2015-2102 RonCoo(http://www.roncoo.com) Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.texasthree.utility.utlis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/**
 * String字符串工具类.
 */
public final class StringUtils {

    private static final Logger LOG = LoggerFactory.getLogger(StringUtils.class);

    /**
     * 私有构造方法,将该工具类设为单例模式.
     */
    private StringUtils() {
    }

    /**
     * 函数功能说明 ： 判断字符串是否为空 . 修改者名字： 修改日期： 修改内容：
     *
     * @return boolean
     * @throws
     * @参数： @param str
     * @参数： @return
     */
    public static boolean isEmpty(String str) {
        return null == str || "".equals(str);
    }

    public static boolean isEmpty(String... args) {
        for (String arg : args) {
            if (isEmpty(arg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 函数功能说明 ： 判断对象数组是否为空. 修改者名字： 修改日期： 修改内容：
     *
     * @return boolean
     * @throws
     * @参数： @param obj
     * @参数： @return
     */
    public static boolean isEmpty(Object[] obj) {
        return null == obj || 0 == obj.length;
    }

    /**
     * 函数功能说明 ： 判断对象是否为空. 修改者名字： 修改日期： 修改内容：
     *
     * @return boolean
     * @throws
     * @参数： @param obj
     * @参数： @return
     */
    public static boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj).trim().isEmpty();
        }
        return !(obj instanceof Number) ? false : false;
    }

    /**
     * 函数功能说明 ： 判断集合是否为空. 修改者名字： 修改日期： 修改内容：
     *
     * @return boolean
     * @throws
     * @参数： @param obj
     * @参数： @return
     */
    public static boolean isEmpty(List<?> obj) {
        return null == obj || obj.isEmpty();
    }

    /**
     * 函数功能说明 ： 判断Map集合是否为空. 修改者名字： 修改日期： 修改内容：
     *
     * @return boolean
     * @throws
     * @参数： @param obj
     * @参数： @return
     */
    public static boolean isEmpty(Map<?, ?> obj) {
        return null == obj || obj.isEmpty();
    }

    /**
     * 函数功能说明 ： 获得文件名的后缀名. 修改者名字： 修改日期： 修改内容：
     *
     * @return String
     * @throws
     * @参数： @param fileName
     * @参数： @return
     */
    public static String getExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 获取去掉横线的长度为32的UUID串.
     *
     * @return uuid.
     * @author WuShuicheng.
     */
    public static String get32UUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取去掉横线的长度为32的UUID串.
     *
     * @return uuid.
     * @author WuShuicheng.
     */
    public static String get16UUID() {
        return get32UUID().substring(0, 16);
    }


    public static String get10UUID() {
        return get32UUID().substring(0, 10);
    }

    /**
     * 获取带横线的长度为36的UUID串.
     *
     * @return uuid.
     * @author WuShuicheng.
     */
    public static String get36UUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 验证一个字符串是否完全由纯数字组成的字符串，当字符串为空时也返回false.
     *
     * @param str 要判断的字符串 .
     * @return true or false .
     * @author WuShuicheng .
     */
    public static boolean isNumeric(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        } else {
            return str.matches("\\d*");
        }
    }

    /**
     * 计算采用utf-8编码方式时字符串所占字节数
     *
     * @param content
     * @return
     */
    public static int getByteSize(String content) {
        int size = 0;
        if (null != content) {
            try {
                // 汉字采用utf-8编码时占3个字节
                size = content.getBytes("utf-8").length;
            } catch (UnsupportedEncodingException e) {
                LOG.error(e.getMessage());
            }
        }
        return size;
    }

    /**
     * 函数功能说明 ： 截取字符串拼接in查询参数. 修改者名字： 修改日期： 修改内容：
     *
     * @return String
     * @throws
     * @参数： @param ids
     * @参数： @return
     */
    public static List<String> getInParam(String param) {
        boolean flag = param.contains(",");
        List<String> list = new ArrayList<String>();
        if (flag) {
            list = Arrays.asList(param.split(","));
        } else {
            list.add(param);
        }
        return list;
    }

    public static String getLastName(Class o) {
        String longName = o.getName();
        String[] s = longName.split("\\.");
        return s[s.length - 1];
    }

    public static String makeForm(Map<String, Object> data) {
        return makeForm(data, false);
    }

    public static String makeForm(Map<String, Object> data, boolean emptyValue) {
        var it = data.entrySet();
        var sb = new StringBuffer();
        for (var entry : it) {
            if (entry.getKey() == null
                    || (StringUtils.isEmpty(entry.getValue()) && !emptyValue)) {
                continue;
            }

            var k = entry.getKey();
            var v = !StringUtils.isEmpty(entry.getValue()) ? entry.getValue().toString() : "";
            sb.append(k + "=" + v + "&");
        }
        var ret = sb.toString();
        return ret.isEmpty() ? ret : ret.substring(0, ret.length() - 1);
    }

    public static String toQueryString(Map<String, Object> parameters) {
        return StringUtils.toQueryString(parameters, StandardCharsets.UTF_8.name());
    }

    public static String toQueryString(Map<String, Object> parameters, String charSet) {
        String queryString = "";
        if (parameters != null && !parameters.isEmpty()) {
            Set<Map.Entry<String, Object>> entrySet = parameters.entrySet();
            for (Map.Entry entry : entrySet) {
                try {
                    String key = entry.getKey().toString();
                    Object value = entry.getValue();
                    List values = StringUtils.makeStringList(value);
                    for (Object v : values) {
                        queryString += key + "=" + URLEncoder.encode(v == null ? "" : v.toString(), charSet) + "&";
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException("invalid charset : " + charSet);
                }
            }
            if (queryString.length() > 0) {
                queryString = queryString.substring(0, queryString.length() - 1);
            }
        }
        return queryString;
    }


    /**
     * @param queryString
     * @param charSet
     * @return
     */
    public static Map<String, Object> queryStringToMap(String queryString, String charSet) {
        if (charSet == null) {
            charSet = StandardCharsets.UTF_8.name();
        }

        int index = queryString.indexOf("?");
        if (index > 0) {
            queryString = queryString.substring(index + 1);
        }
        String[] keyValuePairs = queryString.split("&");
        Map<String, Object> map = new HashMap<>();
        for (String keyValue : keyValuePairs) {
            if (!keyValue.contains("=")) {
                continue;
            }
            String[] args = keyValue.split("=");
            if (args.length == 2) {
                try {
                    map.put(args[0], URLDecoder.decode(args[1], charSet));
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException("invalid charset : " + charSet);
                }
            }
            if (args.length == 1) {
                map.put(args[0], "");
            }
        }
        return map;
    }

    /**
     * @param value
     * @return
     */
    public static List<String> makeStringList(Object value) {
        if (value == null) {
            value = "";
        }
        List<String> result = new ArrayList<String>();
        if (value.getClass().isArray()) {
            for (int j = 0; j < Array.getLength(value); j++) {
                Object obj = Array.get(value, j);
                result.add(obj != null ? obj.toString() : "");
            }
            return result;
        }

        if (value instanceof Iterator) {
            Iterator it = (Iterator) value;
            while (it.hasNext()) {
                Object obj = it.next();
                result.add(obj != null ? obj.toString() : "");
            }
            return result;
        }

        if (value instanceof Collection) {
            for (Object obj : (Collection) value) {
                result.add(obj != null ? obj.toString() : "");
            }
            return result;
        }

        if (value instanceof Enumeration) {
            Enumeration enumeration = (Enumeration) value;
            while (enumeration.hasMoreElements()) {
                Object obj = enumeration.nextElement();
                result.add(obj != null ? obj.toString() : "");
            }
            return result;
        }
        result.add(value.toString());
        return result;
    }

    public static Enum getEnum(Enum[] arry, String name) {
        for (var v : arry) {
            if (v.name().equals(name)) {
                return v;
            }
        }
        return null;
    }

    public static <T extends Enum> List<Map<String, String>> toList(T[] arr, Function<T, String> func) {
        var list = new ArrayList<Map<String, String>>();
        for (var v : arr) {
            var map = new HashMap<String, String>();
            map.put("name", v.name());
            map.put("desc", func.apply(v));
            list.add(map);
        }
        return list;
    }

    private static String[] Surname = {
            "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤",
            "许", "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水",
            "窦", "章", "云", "苏", "潘", "葛", "奚", "范", "彭", "郎", "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞",
            "任", "袁", "柳", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺", "倪", "汤", "滕", "殷", "罗", "毕",
            "郝", "邬", "安", "常", "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平",
            "黄", "和", "穆", "萧", "尹", "姚", "邵", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧", "计", "伏",
            "成", "戴", "谈", "宋", "茅", "庞", "熊", "纪", "舒", "屈", "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席",
            "季"};

    private static String girl = "秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗韵融园艺咏卿聪澜纯悦昭冰爽琬茗羽希宁欣飘育滢柔竹凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽";
    private static String boy = "伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘";

    public static String getChineseName() {
        Random random = new Random();
        int index = random.nextInt(Surname.length - 1);
        //获得一个随机的姓氏
        String name = Surname[index];
        //可以根据这个数设置产生的男女比例
        int i = random.nextInt(3);
        if (i == 2) {
            int j = random.nextInt(girl.length() - 2);
            if (j % 2 == 0) {
                name = name + girl.substring(j, j + 2);
            } else {
                name = name + girl.substring(j, j + 1);
            }
        } else {
            int j = random.nextInt(girl.length() - 2);
            if (j % 2 == 0) {
                name = name + boy.substring(j, j + 2);
            } else {
                name = name + boy.substring(j, j + 1);
            }
        }
        return name;
    }

    public static String plainBigDecimal(BigDecimal amount) {
        return amount.stripTrailingZeros().toPlainString();
    }
}
