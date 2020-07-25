package com.yang.ioc.test;

import com.yang.ioc.framework.annotation.Autowired;

/**
 * @Author: s2892
 * @Date: 2020/7/15 20:57
 */

public class DaLao {

    @Autowired
    private Coffee coffee;

    @Autowired
    private Drinks tea;


    public void drink() {
        System.out.println("大佬喝" + coffee.getType());
        System.out.println("大佬喝" + tea.getType());
    }
}
