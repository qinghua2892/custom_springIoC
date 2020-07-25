package com.yang.ioc.framework;

import com.yang.ioc.framework.annotation.Autowired;
import com.yang.ioc.framework.test.Coffee;
import com.yang.ioc.framework.test.DaLao;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @Author: s2892
 * @Date: 2020/7/15 20:03
 */

public class AppilcationContext {


    public DaLao getDaLao() throws IllegalAccessException {
        DaLao daLao = new DaLao();
        // 获取大佬的所有属性
        Field[] declaredFields = daLao.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            // 修改访问权限
            field.setAccessible(true);
            // 判断方法是否被指定注解
            Autowired annotation = field.getAnnotation(Autowired.class);
            if (annotation != null) {
                // 给有指定注解的属性注入对象
                field.set(daLao, new Coffee());
            }
        }
        return daLao;
    }
}
