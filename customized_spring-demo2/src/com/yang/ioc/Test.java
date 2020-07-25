package com.yang.ioc;

import com.yang.ioc.framework.AppilcationContext;
import com.yang.ioc.test.DaLao;

/**
 * @Author: s2892
 * @Date: 2020/7/15 23:35
 */

public class Test {


    public static void main(String[] args) throws Exception {
        AppilcationContext context = new AppilcationContext();
        DaLao daLao = context.getBean(DaLao.class);
        daLao.drink();
    }
}
