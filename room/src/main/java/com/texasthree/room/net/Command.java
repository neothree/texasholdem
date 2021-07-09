package com.texasthree.room.net;

import java.lang.annotation.*;

/**
 * 网络消息
 *
 * @author: neo
 * @create: 2021-07-09 15:05
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Command {
}
