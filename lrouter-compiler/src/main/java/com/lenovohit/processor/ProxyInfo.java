package com.lenovohit.processor;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * 用于存放注解标注的类信息与生成类信息
 * Created by yuzhijun on 2017/6/21.
 */
public class ProxyInfo {
    public String packageName;
    public String proxyClassName;
    public TypeElement typeElement;
    private String name;
    private String provider;
    private int priority;


    public static final String PROXY = "Inject";

    public ProxyInfo(Elements elementUtils, TypeElement classElement)
    {
        this.typeElement = classElement;
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        String packageName = packageElement.getQualifiedName().toString();
        //classname
        String className = ClassValidator.getClassName(classElement, packageName);
        this.packageName = packageName;
        this.proxyClassName = className + "$$" + PROXY;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
