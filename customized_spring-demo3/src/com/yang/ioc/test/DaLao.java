package com.yang.ioc.test;

import com.yang.ioc.framework.annotation.Autowired;
import com.yang.ioc.framework.annotation.Compoent;

/**
 * @Author: s2892
 * @Date: 2020/7/15 20:57
 */
@Compoent
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
