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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;


/**
 * 日期工具类
 */
public class DateUtils {

    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static final SimpleDateFormat FORMAT_DAY_DATETIME = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat FORMAT_LONG_DATETIME = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat HMS_FORMAT = new SimpleDateFormat("HH:mm:ss");


    // 得到当前日期的星期
    public static int getWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int w = cal.get(Calendar.DAY_OF_WEEK);
        return w;
    }

    /**
     * 根据传入的数字，输出相比现在days天的数据
     *
     * @param days
     * @return Date
     */
    public static Date getDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }


    public static boolean isSameDayWithToday(LocalDateTime date) {
        return date != null && LocalDateTime.now().toLocalDate().compareTo(date.toLocalDate()) == 0;
    }

    public static final DateTimeFormatter LOCAL_SHORT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter LOCAL_LONG_DATETIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static final DateTimeFormatter LOCAL_LONG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter LOCAL_LONG_DATE_FORMAT1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static LocalDateTime parseLocalDateTime(String s) {
        switch (s.length()) {
            case 8:
                LocalDate d = LocalDate.parse(s, DateTimeFormatter.BASIC_ISO_DATE);
                return LocalDateTime.of(d, LocalTime.MIN);
            case 10:
                d = LocalDate.parse(s, LOCAL_SHORT_DATE_FORMAT);
                return LocalDateTime.of(d, LocalTime.MIN);
            case 14:
                return LocalDateTime.parse(s, LOCAL_LONG_DATETIME);
            case 16:
                return LocalDateTime.parse(s, LOCAL_LONG_DATE_FORMAT1);
            case 19:
                return LocalDateTime.parse(s, LOCAL_LONG_DATE_FORMAT);
            default:
                throw new IllegalArgumentException("LocalDateTime 解析错误: " + s);
        }
    }

    public static LocalDateTime dayStart(LocalDate time) {
        return LocalDateTime.of(time, LocalTime.MIN);
    }

    public static LocalDateTime dayStart(LocalDateTime time) {
        return LocalDateTime.of(time.toLocalDate(), LocalTime.MIN);
    }

    public static LocalDateTime dayStart() {
        return dayStart(LocalDateTime.now());
    }

    public static LocalDateTime dayEnd(LocalDate time) {
        return LocalDateTime.of(time, LocalTime.MAX);
    }

    public static LocalDateTime dayEnd(LocalDateTime time) {
        return LocalDateTime.of(time.toLocalDate(), LocalTime.MAX);
    }

    public static LocalDateTime dayEnd() {
        return dayEnd(LocalDateTime.now());
    }

    /**
     * 字符转日期
     *
     * @param s
     * @return
     */
    public static Date parse(String s) {
        SimpleDateFormat formatter = null;
        if (s == null) {
            return null;
        } else if (s.length() == 8) {
            formatter = new SimpleDateFormat("yyyyMMdd");
        } else if (s.length() == 10) {
            formatter = SHORT_DATE_FORMAT;
        } else if (s.length() == 16) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        } else if (s.length() == 19) {
            formatter = LONG_DATE_FORMAT;
        } else if (s.length() > 19) {
            s = s.substring(0, 19);
            formatter = LONG_DATE_FORMAT;
        } else {
            return null;
        }
        try {
            return formatter.parse(s);
        } catch (ParseException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static String toString(Date t) {
        return toString(t, LONG_DATE_FORMAT);
    }

    /**
     * 利用指定SimpleDateFormat instance转换java.util.Date到String
     *
     * @param dt        java.util.Date instance
     * @param formatter SimpleDateFormat Instance
     * @return
     * @history
     * @since 1.0
     */
    public static String toString(Date dt, SimpleDateFormat formatter) {
        try {
            return formatter.format(dt);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }


    public static String toString(LocalDateTime dt, DateTimeFormatter f) {
        try {
            return dt.format(f);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public static String nowString() {
        return toString(LocalDateTime.now());
    }

    public static String toString(LocalDateTime dt) {
        try {
            return dt.format(LOCAL_LONG_DATE_FORMAT);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }


    public static LocalDateTime dateToLocalDateTime(Date d) {
        Instant instant = d.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    public static Date localDateTimeToDate(LocalDateTime d) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = d.atZone(zone).toInstant();
        return Date.from(instant);
    }

    public static Long localDateTimeToSeconds(LocalDateTime d) {
        return d.toEpochSecond(ZoneOffset.of("+8"));
    }

    public static Long localDateTimeToMills(LocalDateTime d) {
        return d.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public static LocalDateTime secondToLocalDateTime(long timestamp) {
        return millisecondToLocalDateTime(timestamp * 1000);
    }

    public static LocalDateTime millisecondToLocalDateTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
