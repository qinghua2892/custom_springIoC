package com.yang.ioc.framework;

import com.yang.ioc.framework.annotation.Autowired;
import com.yang.ioc.framework.annotation.Compoent;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: s2892
 * @Date: 2020/7/15 20:51
 */

public class AppilcationContext {

    public static final Map<Class<?>, List<Class>> fatherSonHashMap = new HashMap<>();


    private String packageName;


    public AppilcationContext() {
        try {
            init("com.yang.ioc");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public AppilcationContext(String packageName) {
        this.packageName = packageName;

        try {
            init(packageName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public <T> T getBean(Class<T> tClass) throws Exception {
        // 传进来的对象
        T t = tClass.newInstance();
        // 获取到传进来的对象所有方法
        Field[] fields = tClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            // 获取到有Autowired注解的属性
            Autowired annotation = field.getAnnotation(Autowired.class);
            if (annotation != null) {
                /*
                     问题: 如果当前类为一个接口,直接通过反射创建对象会报错
                     如果此时为一个接口,有多个实现类,那么怎么确定用哪一个
                */
                // 获取属性声明时类对象
                Class<?> type = field.getType();
                // 判断是否为接口
                if (type.isInterface()) {
                    // 如果对象为一个接口,则需要找到它的实现类
                    // 通过父类找到集合中对应的类集合,即为当前'type'的实现子类
                    List<Class> implClassList = fatherSonHashMap.get(type);
                    if (implClassList != null && implClassList.size() == 1) {
                        // 如果只有一个实现子类则直接注入
                        // 属性注入   参数一:被注入的对象,参数二:需要注入的对象
                        field.set(t, implClassList.get(0).newInstance());
                    } else {
                        // 如有多个或者没有一个匹配上
                        ArrayList<Object> isAutowired = new ArrayList<>();
                        // 多个实现子类,则通过子类的名字匹配
                        for (Class sonClass : implClassList) {
                            // 属性的引用的变量名, 即类名首字母小写
                            String fieldName = field.getName();
                            // 除首字母外的所有
                            String substring = fieldName.substring(1, fieldName.length());
                            // 全部转为大写然后取出第一个
                            char fieldChar = fieldName.toUpperCase().charAt(0);
                            String className = fieldChar + substring;
                            if (field.getName().endsWith(className)) {
                                // 匹配成功
                                field.set(t, sonClass.newInstance());
                                isAutowired.add(sonClass.newInstance());
                            }
                        }
                        // 匹配到多个,或者没有一个匹配上
                        if (isAutowired.size() != 1) {
                            throw new Exception("匹配到多个,或者没有一个匹配上");
                        }
                    }

                } else {
                    // 不为接口  直接注入
                    field.set(t, type.newInstance());
                }
            }
        }

        return t;
    }


    public void init(String packageName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        // 包名
        // String packageName = "com.yang.ioc";
        // classpath根路径
        URL resource = AppilcationContext.class.getResource("/");
        String path = resource.getPath();
        // 拼接包,获取所有文件对象
        String resourcePath = path + "com/yang/ioc/test";
        File file = new File(resourcePath);
        // 获取到所有文件
        File[] files = file.listFiles();
        for (File f : files) {
            // 所有文件的文件名
            String fileName = f.getName();
            // 当前文件是文件且以'.class'结尾即 为一个字节码文件
            if (f.isFile() && fileName.endsWith(".class")) {
                // 以'.'切割,获取到没有后缀的文件名,即类名
                String[] fileNameT = fileName.split("\\.");
                // 使用方式通过包名'.'＋类名获取到所有类对象
                Class<?> aClass = Class.forName(packageName + ".test." + fileNameT[0]);
                // 获取到有Compoent注解的类
                Compoent annotation = aClass.getAnnotation(Compoent.class);
                if (annotation != null) {
                    // 获取到所有的父类
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (int j = 0; j < interfaces.length; j++) {
                        Class<?> ainterfaces = interfaces[j];
                        // 通过父对象判断父子集合是否存在
                        List<Class> sinList = fatherSonHashMap.get(ainterfaces);
                        if (sinList == null) {
                            // 不存在就代表是第一次执行,则需要添加到父子map集合中
                            ArrayList<Class> sonClassList = new ArrayList<>();
                            sonClassList.add(aClass);
                            // key:父类   val:子类集合
                            fatherSonHashMap.put(ainterfaces, sonClassList);
                        } else {
                            // 如存在则将当前对象添加到子集合中
                            sinList.add(aClass);
                        }
                    }
                }
            }
        }
    }


}
