package com.texasthree.room.message;

import java.lang.annotation.*;


@Target({ElementType.TYPE}) //声明自定义的注解使用在方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageController {
}
