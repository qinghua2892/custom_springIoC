package com.yang.ioc.framework;

import com.yang.ioc.framework.test.DaLao;

/**
 * @Author: s2892
 * @Date: 2020/7/15 20:15
 */

public class Demo1 {

    public static void main(String[] args) throws IllegalAccessException {
        AppilcationContext appilcationContext = new AppilcationContext();
        DaLao daLao = appilcationContext.getDaLao();
        daLao.drink();
    }
}
