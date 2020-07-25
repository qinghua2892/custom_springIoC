package com.yang.ioc.framework;

import com.yang.ioc.framework.annotation.Autowired;
import com.yang.ioc.framework.annotation.Compoent;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: s2892
 * @Date: 2020/7/16 23:04
 */

public class ApplicationContext {


    // 存放父类为键,子类集合为值的集合
    public static final HashMap<Class, ArrayList<Class>> fatherSonMap = new HashMap<>();

    // beanmap用来放初始化好的对象。id：对象
    public static final HashMap<String, Object> beansMap = new HashMap<String, Object>();

    private static final List<Class> beansList = new ArrayList<Class>();

    private String basePackage;

    public ApplicationContext(String basePackage) {
        this.basePackage = basePackage;
        try {
            init(basePackage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ApplicationContext() {
    }

    public Object getBean(String id) {
        return beansMap.get(id);
    }

    public Object getBean(Class dalao) {
        String id = getId(dalao);
        return getBean(id);
    }

    private Object inject(Class daLaoClass) throws Exception {
        // 获取到对象模板的id,即首字母小写
        String id = getId(daLaoClass);
        // 判断一个专门存放对象的Map集合里面是否存在,主要是为了保障单例
        Object o = beansMap.get(id);
        if (o == null) {
            // 不存在就加进去
            beansMap.put(id, daLaoClass.newInstance());
        }

        // 获取到所有的属性
        Field[] allField = daLaoClass.getDeclaredFields();
        for (Field field : allField) {
            // 暴力破解私有属性
            field.setAccessible(true);
            Autowired annotation = field.getAnnotation(Autowired.class);
            if (annotation != null) {
                // 获取到属性的类型
                Class<?> fieldType = field.getType();
                if (fieldType.isInterface()) {
                    // 属性的类型为接口,则需要所有实现类注入
                    // 通过属性类型,即为属性对象的父类,获取到属性子类
                    ArrayList<Class> sonClasses = fatherSonMap.get(fieldType);
                    if (sonClasses == null) {
                        // 需要注入属性的类型为接口,但又没有实现子类,
                        throw new NullPointerException();
                    } else if (sonClasses.size() == 1) {
                        // 实现子类只有一个则直接注入
                        // 保障单例,
                        String id1 = getId(sonClasses.get(0));
                        Object o1 = beansMap.get(id1);
                        if (o1 == null) {
                            // 通过class模板创建实例,将属性注入实例
                            Object instance = sonClasses.get(0).newInstance();
                            field.set(daLaoClass, instance);
                            // 并将对象及唯一标识存入集合
                            beansMap.put(id1, instance);
                        } else {
                            field.set(daLaoClass, o1);
                        }
                    } else {
                        // 多个实现对象
                        ArrayList<Object> isAutowired = new ArrayList<>();
                        for (Class sonClass : sonClasses) {
                            // 属性的引用的变量名, 即类名首字母小写
                            String fieldName = field.getName();
                            // 获取除首字母外的所有字母
                            String substring = fieldName.substring(1, fieldName.length());
                            // 将所有字母大写,并取出第一个
                            char fristChar = fieldName.toUpperCase().charAt(0);
                            // 拼接结果为属性的引用的变量名首字母大写,即于类名一致
                            String className = fristChar + substring;
                            if (sonClass.getName().endsWith(className)) {
                                // 通过名字匹配上了
                                // 保障单例
                                String id3 = getId(sonClass);
                                Object o1 = beansMap.get(id3);
                                if (o1 == null) {
                                    // 通过class模板创建实例,将属性注入实例
                                    Object instance = sonClass.newInstance();
                                    field.set(daLaoClass, instance);
                                    // 并将对象及唯一标识存入集合
                                    beansMap.put(id3, instance);
                                } else {
                                    field.set(daLaoClass, o1);
                                }
                            }
                            if (isAutowired.size() > 1) {
                                throw new Exception();
                            }
                        }
                    }
                } else {
                    // 不为接口
                    // 保障单例,   通过类模板获取到唯一标识
                    String id1 = getId(fieldType);
                    // 通过类模板的唯一标识获取到当前模板的实例
                    Object o1 = beansMap.get(id1);
                    if (o1 == null) {
                        // 如果没有对应的实例,则通过类模板创建对应的实例
                        Object instance = fieldType.newInstance();
                        // 注入属性对象,并存入集合
                        field.set(daLaoClass, instance);
                        beansMap.put(id1, instance);
                    } else {
                        // 有对应的实例则直接注入
                        field.set(daLaoClass, o1);
                    }
                }
            }
        }
        return daLaoClass;
    }


    /**
     * 初始化,即扫描所有compoent注解标注的类
     */
    public void init(String scanPack) throws Exception {
        // 获取到当前项目的classpath
        URL resource = ApplicationContext.class.getResource("/");
        // 拼接传来的指定包,获取到需要扫描包的绝对路径(基于电脑)
        String classPathF = resource.getPath() + scanPack.replaceAll("\\.", "/");
        // 获取到指定文件
        File file = new File(classPathF);
        File[] files = file.listFiles();

        for (File fileName : files) {
            // 判断文件是否为class文件,即是否为类
            if (fileName.isFile() && fileName.getName().endsWith(".class")) {
                // 获取到的文件名通过'.'切割得到类名
                String[] split = fileName.getName().split("\\.");
                // 拼接包名+类名得到全限定名,反射通过全限定名获取到类对象
                String classAllName = scanPack + "." + split[0];
                Class<?> aClass = Class.forName(classAllName);
                Compoent annotation = aClass.getAnnotation(Compoent.class);
                if (annotation != null) {

                    // 有Compoent注解的对象存入集合中
                    beansList.add(aClass);

                    // 获取到了标注了Compoent注解的类模板
                    // 获取类模板的父类
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        // 通过父类获取到实现子类
                        ArrayList<Class> sonList = fatherSonMap.get(anInterface);
                        if (sonList == null) {
                            // 如果子类为空,则为第一次找到当前father类,就保存到Map集合中
                            ArrayList<Class> sonClassList = new ArrayList<>();
                            sonClassList.add(aClass);
                            fatherSonMap.put(anInterface, sonClassList);
                        } else {
                            // 非第一次找到,则将子类对象根据对应的父类存入ArrayList集合中
                            sonList.add(aClass);
                        }
                    }
                }

            }
        }
        // 遍历所有的被Compoent标注的类
        for (int i = 0, size = beansList.size(); i < size; i++) {
            Class aClass = beansList.get(i);
            // 对象注入
            Object obj = inject(aClass);
            // 获取到类模板的id
            String id = getId(aClass);
            // 存入初始化好的对象。
            beansMap.put(id, obj);

        }

    }


    /**
     * 获取类模板对应的Id, 即类名首字母小写作为唯一标识(id)
     *
     * @param aClass
     * @return
     */
    private String getId(Class aClass) {
        // 获取类模板的全限定名
        String tempId = aClass.getName();
        //获取最后一个“.”的下标
        int lastIndex = tempId.lastIndexOf(".");
        tempId = tempId.substring(lastIndex + 1, tempId.length());
        char firstChar = tempId.toLowerCase().charAt(0);
        String substring = tempId.substring(1, tempId.length());
        String id = firstChar + substring;
        return id;
    }


}
