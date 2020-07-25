package com.yang.ioc.test;

import com.yang.ioc.framework.annotation.Compoent;

/**
 * @Author: s2892
 * @Date: 2020/7/15 20:54
 */

@Compoent
public class Tea implements Drinks {

    @Override
    public String getType() {
        return "乌龙茶";
    }
}
