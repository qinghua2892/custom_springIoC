package com.yang.ioc.framework.test;

import com.yang.ioc.framework.annotation.Autowired;

/**
 * @Author: s2892
 * @Date: 2020/7/15 20:06
 */

public class DaLao {


    @Autowired
    private Coffee coffee;


    public void drink() {
        System.out.println("大佬喝" + coffee.getCoffee());
    }

}
