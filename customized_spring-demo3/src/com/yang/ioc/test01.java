package com.yang.ioc;

import com.yang.ioc.framework.ApplicationContext;
import com.yang.ioc.test.DaLao;

/**
 * @Author: s2892
 * @Date: 2020/7/17 1:14
 */

public class test01 {

    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext("com.yang.ioc.test");
        DaLao bean = (DaLao) context.getBean("dalao");
        bean.drink();
    }
}
