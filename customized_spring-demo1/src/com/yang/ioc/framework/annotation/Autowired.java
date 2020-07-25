package com.yang.ioc.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: s2892
 * @Date: 2020/7/15 20:04
 */

@Retention(RetentionPolicy.RUNTIME) // 在运行时有效
@Target(ElementType.FIELD)// 作用在属性上
public @interface Autowired {
}
