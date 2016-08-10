package com.example.ioc.processor;

/**
 * 描    述：
 * 作    者：liyx@13322.com
 * 时    间：2016/8/8
 */
public interface AbstractInjector<T> {

    /**
     *
     * @param target
     *          需要inject的变量属于的类
     * @param source
     *          通过哪个对象（View 或者 Activity ）找到需要inject的变量
     */
    void inject(T target , Object source);
}
