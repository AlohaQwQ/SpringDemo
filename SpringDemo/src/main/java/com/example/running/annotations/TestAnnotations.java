package com.example.running.annotations;

import java.lang.annotation.*;

/**
 * @author Aloha
 * @date 2022/11/15 15:03
 * @description 注解
 */
//@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestAnnotations {

    int age() default 1;

    String name() default "";


}
